package com.ex.service;

import com.ex.data.PhotoDTO;
import com.ex.entity.ProductEntity;
import com.ex.entity.ProductImgEntity;
import com.ex.entity.ProductThumbnailEntity;
import com.ex.repository.ProductImgRepository;
import com.ex.repository.ProductThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PhotoService {
  
    private final ProductThumbnailRepository productThumbnailRepository;
    private final ProductImgRepository productImgRepository;

    private final String THUMBNAIL_DIR = "F:/img/thumbnails/";
    private final String DESCRIPTION_DIR = "F:/img/descriptions/";

    public void saveThumbnails(ProductEntity productEntity, MultipartFile[] thumbnails) throws IOException {
        for (MultipartFile file : thumbnails) {
            String storedFilename = saveFile(file, THUMBNAIL_DIR);
            if (storedFilename != null) {
                ProductThumbnailEntity thumbnail = ProductThumbnailEntity.builder()
                        .product(productEntity)
                        .orgname(file.getOriginalFilename())
                        .sysname(storedFilename)
                        .build();
                productThumbnailRepository.save(thumbnail);
            }
        }
    }

    public void saveDescriptionImages(ProductEntity productEntity, MultipartFile[] descriptionImages) throws IOException {
        for (MultipartFile file : descriptionImages) {
            String storedFilename = saveFile(file, DESCRIPTION_DIR);
            if (storedFilename != null) {
                ProductImgEntity descriptionImage = ProductImgEntity.builder()
                        .product(productEntity)
                        .orgname(file.getOriginalFilename())
                        .sysname(storedFilename)
                        .build();
                productImgRepository.save(descriptionImage);
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

    public List<PhotoDTO> getThumbnailPaths(Integer productId) {
        List<ProductThumbnailEntity> thumbnails = productThumbnailRepository.findByProductIdOrderByIdAsc(productId);
        List<PhotoDTO> photoDTOList = new ArrayList<>();
        for (ProductThumbnailEntity thumbnail : thumbnails) {
            photoDTOList.add(new PhotoDTO(
                    thumbnail.getId(),
                    thumbnail.getOrgname(),
                    thumbnail.getSysname(),
                    "/file/display?filename=thumbnails/" + thumbnail.getSysname()
            ));
        }
        return photoDTOList;
    }

    public List<PhotoDTO> getDescriptionImagePaths(Integer productId) {
        List<ProductImgEntity> descriptionImages = productImgRepository.findByProductIdOrderByIdAsc(productId);
        List<PhotoDTO> photoDTOList = new ArrayList<>();
        for (ProductImgEntity image : descriptionImages) {
            photoDTOList.add(new PhotoDTO(
                    image.getId(),
                    image.getOrgname(),
                    image.getSysname(),
                    "/file/display?filename=descriptions/" + image.getSysname()
            ));
        }
        return photoDTOList;
    }

    public void deleteThumbnail(Integer id) {
        ProductThumbnailEntity thumbnail = productThumbnailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thumbnail not found"));
        File file = new File(THUMBNAIL_DIR + thumbnail.getSysname());
        if (file.exists()) {
            file.delete();
        }
        productThumbnailRepository.deleteById(id);
    }

    public void deleteDescriptionImage(Integer id) {
        ProductImgEntity descriptionImage = productImgRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Description image not found"));
        File file = new File(DESCRIPTION_DIR + descriptionImage.getSysname());
        if (file.exists()) {
            file.delete();
        }
        productImgRepository.deleteById(id);
    }

    public void deleteThumbnails(List<Integer> ids) {
        for (Integer id : ids) {
            deleteThumbnail(id);
        }
    }

    public void deleteDescriptionImages(List<Integer> ids) {
        for (Integer id : ids) {
            deleteDescriptionImage(id);
        }
    }
    
    public List<ProductThumbnailEntity> findSysname(Integer id){
    	List<ProductThumbnailEntity> pes = productThumbnailRepository.findByProductIdOrderByIdAsc(id);
    	return pes;
    }
}
