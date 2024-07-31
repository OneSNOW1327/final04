package com.ex.service;
import com.ex.data.ProductDTO;
import com.ex.entity.BasketEntity;
import com.ex.entity.ProductEntity;
import com.ex.entity.ProductImgEntity;
import com.ex.entity.ProductThumbnailEntity;
import com.ex.entity.ProducttypeEntity;
import com.ex.entity.UserEntity;
import com.ex.repository.BasketRepository;
import com.ex.repository.ProductImgRepository;
import com.ex.repository.ProductRepository;
import com.ex.repository.ProductThumbnailRepository;
import com.ex.repository.ProducttypeRepository;
import com.ex.repository.UserRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
@Service
@RequiredArgsConstructor
public class ProductService {
	private final UserService userService;
	private final UserRepository userRepository;
	private final BasketRepository basketRepository;
	private final ProductRepository productRepository;
	private final ProducttypeRepository producttypeRepository;
	private final ProductImgRepository productImgRepository;
	private final ProductThumbnailRepository productThumbnailRepository;
	private final PhotoService photoService;

	public List<ProductEntity> allProduct() {
		return productRepository.findAll();
	}
	public List<ProducttypeEntity> getAllProductTypes() {
		return producttypeRepository.findAll();
	}
	@Transactional
	public Integer createOrUpdate(ProductDTO productDTO, MultipartFile[] thumbnails, MultipartFile[] descriptionImages, List<Integer> deleteThumbnailIds, List<Integer> deleteDescriptionImageIds) throws IOException {
		Optional<ProducttypeEntity> optionalProductType = producttypeRepository.findById(productDTO.getTypeId());
		if (!optionalProductType.isPresent()) {
			throw new IllegalArgumentException("Invalid typeId");
		}
		ProducttypeEntity productType = optionalProductType.get();

		ProductEntity productEntity;
		if (productDTO.getId() != null) {
			productEntity = productRepository.findById(productDTO.getId()).orElseThrow(() -> new RuntimeException("Product not found"));
			productEntity.setName(productDTO.getName());
			productEntity.setDescription(productDTO.getDescription());
			productEntity.setBuyPrice(productDTO.getBuyPrice());
			productEntity.setSellPrice(productDTO.getSellPrice());
			productEntity.setDiscount(productDTO.getDiscount());
			productEntity.setStock(productDTO.getStock());
			productEntity.setType(productType);
			productEntity.setOrderEmail(productDTO.getOrderEmail());

			// 기존 사진 삭제
			if (deleteThumbnailIds != null) {
				photoService.deleteThumbnails(deleteThumbnailIds);
			}
			if (deleteDescriptionImageIds != null) {
				photoService.deleteDescriptionImages(deleteDescriptionImageIds);
			}
		} else {
			productEntity = ProductEntity.builder()
					.name(productDTO.getName())
					.description(productDTO.getDescription())
					.buyPrice(productDTO.getBuyPrice())
					.sellPrice(productDTO.getSellPrice())
					.discount(productDTO.getDiscount())
					.stock(productDTO.getStock())
					.type(productType)
					.registrationDate(LocalDateTime.now())
					.orderEmail(productDTO.getOrderEmail())
					.build();
		}

		productRepository.save(productEntity);

		// 썸네일 및 설명 이미지 저장
		if (thumbnails != null) {
			photoService.saveThumbnails(productEntity, thumbnails);
		}
		if (descriptionImages != null) {
			photoService.saveDescriptionImages(productEntity, descriptionImages);
		}

		return productEntity.getId();
	}

	public ProductDTO findById(Integer id) {
		Optional<ProductEntity> op = productRepository.findById(id);
		if (op.isPresent()) {
			ProductEntity pe = op.get();
			ProductDTO productDTO = ProductDTO.builder()
					.id(pe.getId())
					.type(pe.getType())
					.name(pe.getName())
					.description(pe.getDescription())
					.discount(pe.getDiscount())
					.buyPrice(pe.getBuyPrice())
					.sellPrice(pe.getSellPrice())
					.stock(pe.getStock())
					.registrationDate(pe.getRegistrationDate())
					.orderEmail(pe.getOrderEmail())
					.build();
			return productDTO;
		} else {
			throw new RuntimeException("Product not found");
		}
	}

	@Transactional
	public void deleteProduct(Integer id) {
		// 사진 파일 및 데이터베이스에서 사진 관련 정보 삭제
		List<ProductImgEntity> descriptionImages = productImgRepository.findByProductIdOrderByIdAsc(id);
		List<Integer> descriptionImageIds = new ArrayList<>();
		for (ProductImgEntity image : descriptionImages) {
			descriptionImageIds.add(image.getId());
		}
		photoService.deleteDescriptionImages(descriptionImageIds);

		List<ProductThumbnailEntity> thumbnails = productThumbnailRepository.findByProductIdOrderByIdAsc(id);
		List<Integer> thumbnailIds = new ArrayList<>();
		for (ProductThumbnailEntity thumbnail : thumbnails) {
			thumbnailIds.add(thumbnail.getId());
		}
		photoService.deleteThumbnails(thumbnailIds);

		// 제품 삭제
		productRepository.deleteById(id);
	}

	//통합검색 페이징 처리
	public Page<ProductEntity> productAll(int page, String kw){
		//Sort.Order DB 쿼리 결과를 ★특정 필드★를 기준으로 정렬
		List<Sort.Order> sorts = new ArrayList<>();
		// ★특정 필드★:registrationDate 기준 내림차순
		sorts.add(Sort.Order.desc("registrationDate"));
		/*search(kw): kw에 대한 Specification 객체를 생성
		 생성된 Specification 객체는 ProductEntity의 특정 필드가 검색 키워드를 포함하는지 여부를 확인하는 쿼리를 생성*/
		Specification<ProductEntity> spec = search(kw);		      				
		//.findAll() 데이터베이스에서 특정 엔티티 타입의 모든 레코드를 반환
		return productRepository.findAll(spec, PageRequest.of(page,10,Sort.by(sorts)));
	}

	//통합검색	__통합검색 페이징 처리에서 사용
	public void addToOrderList(Integer productId, String userName) {
		Optional<ProductEntity> pop = productRepository.findById(productId);
		UserEntity ue = userService.findByUserName(userName);
	}
	//검색 키워드에 해당하는 ProductEntity를 찾는 데 필요한 검색 조건을 설정
	private Specification<ProductEntity> search(String kw){
		return new Specification<>() {
			//ProductEntity를 검색
			@Override
			public Predicate toPredicate(Root<ProductEntity> root,  
					//쿼리문
					CriteriaQuery<?> query,
					//쿼리실행
					CriteriaBuilder criteriaBuilder) {
				//중복제거
				query.distinct(true);
				Join<ProductEntity, ProducttypeEntity> u1 = root.join("type", JoinType.LEFT);
				return criteriaBuilder.or(criteriaBuilder.like(root.get("name"), "%"+kw+"%"),
						criteriaBuilder.like(root.get("description"), "%"+kw+"%"),
						criteriaBuilder.like(u1.get("typename"), "%"+kw+"%"));
			}
		};
	}

	public Page<ProductEntity> typeList(Integer id,int page){
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("registrationDate"));
		return productRepository.findByTypeId(id, PageRequest.of(page,12,Sort.by(sorts)));
	}

	//장바구니 선택상품 삭제
	@Transactional//데이터베이스 작업의 원자성, 일관성, 격리성, 내구성을 보장하기 위해 사용
	public void removeSelectedBaskets(List<Integer> basketIds) {
		List<BasketEntity> basketsToRemove = basketRepository.findAllById(basketIds);
		basketRepository.deleteAll(basketsToRemove);
	}

	//결제페이지 결제예정 상품리스트
	public List<BasketEntity> expectPay(List<Integer> basketIds) {  		
		List<BasketEntity> basketsToPay = basketRepository.findAllById(basketIds);         		
		return basketRepository.saveAll(basketsToPay);		 
	}

	//장바구니 추가
	public void addToBasket(Integer productId, String userName, int quantity) {
		Optional<ProductEntity> pop = productRepository.findById(productId);
		UserEntity ue = userService.findByUserName(userName);
		BasketEntity be = BasketEntity.builder()
				.product(pop.get())
				.user(ue)
				.quantity(quantity)
				.build();
		basketRepository.save(be);        
	}

	//장바구니 현황_로그인유저에 대한 BasketEntity
	public List<BasketEntity> userBasket(String userName) {
		UserEntity ue = this.userService.findByUserName(userName);
		List<BasketEntity> userBasket = ue.getBasketList(); // 유저의 장바구니 리스트
		return userBasket;
	}  	

	//찜♥ 추가
	public void addToWish(Integer productId, String userName) {
		Optional<ProductEntity> pop = this.productRepository.findById(productId);
		Optional<UserEntity> uop = this.userRepository.findByUsername(userName);
		if(pop.isPresent()&&uop.isPresent()) {
			ProductEntity pe = pop.get();
			UserEntity ue = uop.get();
			if(!ue.getWishList().contains(pe)) {
				ue.getWishList().add(pe);
				userRepository.save(ue);
			}
		}  		
	}

}
