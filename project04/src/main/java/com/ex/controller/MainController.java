package com.ex.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ex.data.FaqDTO;
import com.ex.data.NoticeDTO;
import com.ex.entity.NoticeEntity;
import com.ex.entity.ProductThumbnailEntity;
import com.ex.entity.ProducttypeEntity;
import com.ex.service.FaqService;
import com.ex.service.NoticeService;
import com.ex.service.PhotoService;
import com.ex.service.ProductService;

import lombok.RequiredArgsConstructor;
 
@Controller
@RequiredArgsConstructor
public class MainController {

	private final NoticeService noticeService;
	private final ProductService productService;
	private final PhotoService photoService;
    private final FaqService faqService;
 
	@GetMapping("/main")
	public String mainPage(Model model) {
	    model.addAttribute("typeList", productService.getAllProductTypes());
	    List<ProducttypeEntity> productTypes = productService.getSortedProductTypes();
	    model.addAttribute("productTypes", productTypes);

	    NoticeDTO notice = noticeService.getMainNotice();
	    model.addAttribute("notice", notice);

	    // 공지사항 이미지 존재 여부 확인
	    boolean hasNoticePhoto = notice != null && notice.getNoticePhotoPath() != null && !notice.getNoticePhotoPath().isEmpty();
	    model.addAttribute("hasNoticePhoto", hasNoticePhoto);

	    return "main";
	}

	@GetMapping("/")
	public String main() {
		return "redirect:/main";
	}

	// 사진업로드
	@GetMapping("/file/display")
	public ResponseEntity<Resource> display(@RequestParam("folder") String folder, @RequestParam("filename") String filename) {
		//ResponseEntity: HTTP 상태 코드, 헤더, 본문을 포함하여 HTTP 응답의 모든 부분을 캡슐화
		//Resource(I): 추상화를 제공 리소스를 로드하고 처리하는 일관된 방법을 제공
		String basePath = "F:/img/";
		String fullPath = basePath + folder + "/" + filename;

		//Resource(I): 파일명을 포함한 파일 경로로 FileSystemResource(구현체중 하나) 객체를 생성 - 웹서버에 파일이 없기때문에 업로드된 파일을 읽어온다.
		Resource resource = new FileSystemResource(fullPath);

		// 파일 있는지 확인
		if(!resource.exists()) 
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND); // 파일이 없으면 404 not Found 전달

		// HTTP 응답 이미지 정보를 설정하기위한 객체
		HttpHeaders header = new HttpHeaders(); 
		Path filePath = null;
		try{
			// 파일의 MIME 타입을 결정하기위해 Path 클래스로 변환
			filePath = Paths.get(fullPath);
			// HTTP 응답이미지 타입으로 헤더에 추가 
			header.add("Content-type", Files.probeContentType(filePath));
		}catch(IOException e) {
			e.printStackTrace();
		}
		// 파일 리소스와 200 OK 상태 코드를 포함한 ResponseEntity 객체를 반환 / 이래야 브라우저에서 다운로드및 이미지 출려이 가능하다.
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}

	@GetMapping("/file/list/{id}")
	public ResponseEntity<List<String>> getImageList(@PathVariable("id") Integer id) {
		List<String> imageList = photoService.findSysname(id)
				.stream()
				.map(ProductThumbnailEntity::getSysname)
				.collect(Collectors.toList());
		return new ResponseEntity<>(imageList, HttpStatus.OK);
	}
	
	// 공지사항 상세 보기
	@GetMapping("/NoticeDetail/{id}")
	public String noticeDetail(@PathVariable("id") Integer id, Model model) {
		NoticeDTO noticeDTO = noticeService.findById(id); // 공지사항 정보 불러오기
		noticeDTO.setNoticePhotoPath(photoService.getNotice(id)); // 이미지 경로 설정
		model.addAttribute("NoticeDTO", noticeDTO); // 모델에 NoticeDTO 추가
		return "NoticeDetail"; // 템플릿 반환
	}
	
	@GetMapping("/NoticeList")
	public String list(@RequestParam(value = "page", defaultValue = "1") int page, Model model) {
		if (page <= 0) {
			return "redirect:/NoticeList?page=1";
		}
		
		Page<NoticeEntity> noticePage = noticeService.noticeAll(page - 1);
		model.addAttribute("noticeList", noticePage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", noticePage.getTotalPages());
		return "NoticeList";
	}
	
    // FAQ 리스트를 표시하는 메서드
    @GetMapping("/faqList")
    public String showFaqList(Model model, Principal principal) {
        List<FaqDTO> faqs = faqService.getAllFaqs();  // 모든 FAQ를 조회
        model.addAttribute("faqs", faqs);  // 조회된 FAQ 리스트를 모델에 추가

        return "faqList";  // FAQ 리스트를 표시하는 뷰로 이동
    }
	
	
}
