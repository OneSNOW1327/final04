package com.ex.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ex.entity.ProductThumbnailEntity;
import com.ex.service.PhotoService;
import com.ex.service.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainController {
	
	private final ProductService productService;
	private final PhotoService photoService;
	
    @GetMapping("/main")
    public String mainPage(Model model) {    	
    	model.addAttribute("productDTO", productService.allProduct());
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
	
}
