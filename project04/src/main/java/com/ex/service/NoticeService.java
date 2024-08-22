package com.ex.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ex.data.NoticeDTO;
import com.ex.data.PhotoDTO;
import com.ex.entity.NoticeEntity;
import com.ex.entity.NoticePhotoEntity;
import com.ex.repository.NoticePhotoRepository;
import com.ex.repository.NoticeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
	
	private final NoticePhotoRepository noticePhotoRepository;
	private final NoticeRepository noticeRepository;
    private final String NOTICE_DIR = "F:/img/notice/";
	
	@Transactional
	public Integer NoticecreateOrUpdate(NoticeDTO noticeDTO, MultipartFile[] notice) throws IOException {
		NoticeEntity noticeEntity;
		if (noticeDTO.getId() != null) {
			noticeEntity = noticeRepository.findById(noticeDTO.getId())
					.orElseThrow(() -> new RuntimeException("Product not found"));
			noticeEntity.setName(noticeDTO.getName());
			noticeEntity.setDescription(noticeDTO.getDescription());
		} else {
			noticeEntity = NoticeEntity.builder()
					.name(noticeDTO.getName())
					.description(noticeDTO.getDescription())
					.build();
		}
		noticeRepository.save(noticeEntity);
		// 썸네일 및 설명 이미지 저장
		if (notice != null) {
			saveNoticePhoto(noticeEntity, notice);
		}
		return noticeEntity.getId();
	}
	
	public void saveNoticePhoto(NoticeEntity noticeEntity, MultipartFile[] notice) throws IOException {
		for (MultipartFile file : notice) {
			String storedFilename = saveFile(file, NOTICE_DIR);
			if (storedFilename != null) {
				NoticePhotoEntity noticephoto = NoticePhotoEntity.builder().notice(noticeEntity)
																															.orgname(file.getOriginalFilename())
																															.sysname(storedFilename)
																															.build();
				noticePhotoRepository.save(noticephoto);
			}
		}
	}
	
	private String saveFile(MultipartFile file, String dir) throws IOException {
		if (file.isEmpty()) {
			return null;
		}
		
		File directory = new File(dir);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		String originalFilename = file.getOriginalFilename();
		String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
		Files.copy(file.getInputStream(), Paths.get(dir + storedFilename));
		return storedFilename;
	}
	
	public List<NoticeEntity> allNotice() {
		return noticeRepository.findAll();
	}
	
	public NoticeDTO findById(Integer id) {
		Optional<NoticeEntity> np = noticeRepository.findById(id);
		if (np.isPresent()) {
			return NoticeDTO.entityToDTO(np.get());
		} else {
			throw new RuntimeException("Product not found");
		}
	}
	
	public Page<NoticeEntity> noticeAll(int page) {
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("id"));
		return noticeRepository.findAll(PageRequest.of(page, 10, Sort.by(sorts)));
	}
	
	public List<PhotoDTO> getNotice(Integer id) {
		List<NoticePhotoEntity> noticePhotos = noticePhotoRepository.findByNoticeIdOrderByIdAsc(id);
		List<PhotoDTO> photoDTOList = new ArrayList<>();
		for (NoticePhotoEntity noticePhoto : noticePhotos) {
			photoDTOList.add(new PhotoDTO(
					noticePhoto.getId(),
					noticePhoto.getOrgname(),
					noticePhoto.getSysname(),
					"/file/display?folder=notice&filename=" + noticePhoto.getSysname())
			);
		}
		return photoDTOList;
	}
	
	public NoticeDTO getMainNotice() {
		NoticeEntity mainNotice = noticeRepository.findByMain(1);
		if (mainNotice != null) {
			NoticeDTO noticeDTO = NoticeDTO.entityToDTO(mainNotice);
			noticeDTO.setNoticePhotoPath(getNoticePhotoPaths(mainNotice.getId()));
			return noticeDTO;
		}
		return null; // 공지사항이 없는 경우 null 반환
	}
	
	private List<PhotoDTO> getNoticePhotoPaths(Integer noticeId) {
		List<NoticePhotoEntity> photos = noticePhotoRepository.findByNoticeIdOrderByIdAsc(noticeId);
		List<PhotoDTO> photoDTOList = new ArrayList<>();
		for (NoticePhotoEntity photo : photos) {
			photoDTOList.add(new PhotoDTO(
					photo.getId(),
					photo.getOrgname(),
					photo.getSysname(),
					"/file/display?folder=notice&filename=" + photo.getSysname())
			);
		}
		return photoDTOList;
	}
	
	public void changeMainNotice(Integer id) {
		NoticeEntity oldMainNotice = noticeRepository.findByMain(1);
		oldMainNotice.setMain(0);
		noticeRepository.save(oldMainNotice);
		NoticeEntity newMainNotice = noticeRepository.findById(id).get();
		newMainNotice.setMain(1);
		noticeRepository.save(newMainNotice);
	}
	
}
