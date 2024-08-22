package com.ex.service;

import com.ex.data.AmountDTO;
import com.ex.data.DeliveryDTO;
import com.ex.data.ProductDTO;
import com.ex.entity.BasketEntity;
import com.ex.entity.DeliveryEntity;
import com.ex.entity.OrderlistEntity;
import com.ex.entity.ProductEntity;
import com.ex.entity.ProductImgEntity;
import com.ex.entity.ProductThumbnailEntity;
import com.ex.entity.ProducttypeEntity;
import com.ex.entity.SalesVolumeEntity;
import com.ex.entity.UserEntity;
import com.ex.repository.BasketRepository;
import com.ex.repository.DeliveryRepository;
import com.ex.repository.OrderlistRepository;
import com.ex.repository.ProductImgRepository;
import com.ex.repository.ProductRepository;
import com.ex.repository.ProductThumbnailRepository;
import com.ex.repository.ProducttypeRepository;
import com.ex.repository.SalesVolumeRepository;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
  
@Service
@RequiredArgsConstructor
@EnableScheduling
public class ProductService {
	
	private final AdminService adminService;
	private final PhotoService photoService;
	private final UserService userService;
	private final EmailService emailService;
	private final UserRepository userRepository;
	private final BasketRepository basketRepository;
	private final ProductRepository productRepository;
	private final ProducttypeRepository producttypeRepository;
	private final ProductImgRepository productImgRepository;
	private final ProductThumbnailRepository productThumbnailRepository;
	private final SalesVolumeRepository salesVolumeRepository;
	private final OrderlistRepository orderlistRepository;
	private final DeliveryRepository deliveryRepository;

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
			return ProductDTO.entityToDTO(op.get());
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

	//0801 성진
	public Page<ProductEntity> productSearch(int page, String kw){
		//Sort.Order DB 쿼리 결과를 ★특정 필드★를 기준으로 정렬
		List<Sort.Order> sorts = new ArrayList<>();
		// ★특정 필드★:제품 타입별 정렬
		sorts.add(Sort.Order.asc("type_id"));
		/*search(kw): kw에 대한 Specification 객체를 생성
		 생성된 Specification 객체는 ProductEntity의 특정 필드가 검색 키워드를 포함하는지 여부를 확인하는 쿼리를 생성*/
		Specification<ProductEntity> spec = search(kw);		      				
		//.findAll() 데이터베이스에서 특정 엔티티 타입의 모든 레코드를 반환
		return productRepository.findAll(spec, PageRequest.of(page,12,Sort.by(sorts)));
	}

	//검색 키워드에 해당하는 ProductEntity를 찾는 데 필요한 검색 조건을 설정
	private Specification<ProductEntity> search(String kw){
		return new Specification<>() {
			//ProductEntity를 검색
			@Override
			public Predicate toPredicate(Root<ProductEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
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
	
//(가은) 장바구니 현황_로그인유저에 대한 BasketEntity
  	public List<BasketEntity> userBasket(String userName) {
  		UserEntity ue = this.userService.findByUserName(userName);
  		List<BasketEntity> userBasket = ue.getBasketList(); // 유저의 장바구니 리스트
  	    return userBasket;
  	}
  	
//(가은) 장바구니 선택상품 삭제
	@Transactional//데이터베이스 작업의 원자성, 일관성, 격리성, 내구성을 보장하기 위해 사용
	public void removeSelectedBaskets(List<Integer> basketIds) {
		List<BasketEntity> basketsToRemove = basketRepository.findAllById(basketIds);
		basketRepository.deleteAll(basketsToRemove);
	}
	
	//(가은) 장바구니 추가
	public void addToBasket(Integer productId, String userName, int quantity) {
		Optional<ProductEntity> pop = this.productRepository.findById(productId);
		Optional<UserEntity> uop = this.userRepository.findByUsername(userName);
		if (pop.isPresent() && uop.isPresent()) {
			ProductEntity pe = pop.get();
			UserEntity ue = uop.get();
			Optional<BasketEntity> bop = basketRepository.findByUserIdAndProductId(ue.getId(), productId);
			if (!bop.isPresent()) {
				BasketEntity be = BasketEntity.builder()
						.product(pe)
						.user(ue)
						.quantity(quantity)
						.build();
				basketRepository.save(be);
			} else {
				BasketEntity be = bop.get();
				be.setQuantity(be.getQuantity() + quantity);
				basketRepository.save(be);
			}
		}
	}
	
	//(가은) DIRECT결제시 임시 장바구니 생성
	public BasketEntity createTemporaryBasketItem(Integer productId, String userName, int quantity) {
		Optional<ProductEntity> pop = this.productRepository.findById(productId);
		Optional<UserEntity> uop = this.userRepository.findByUsername(userName);
		if (pop.isPresent() && uop.isPresent()) {
			ProductEntity pe = pop.get();
			UserEntity ue = uop.get();
			// 새로운 임시 장바구니 항목 생성
			BasketEntity be = BasketEntity.builder()
					.product(pe)
					.user(ue)
					.quantity(quantity)
					.build();
			return be; //save 하지 않고 넘김
		}
		return null;
	}
		
//(가은) 장바구니 수량 변경 
	public void updateQuantity(int basketIds,int quantity) {
		Optional<BasketEntity> bop = basketRepository.findById(basketIds);
		BasketEntity basketEntity= bop.get();
		basketEntity.setQuantity(quantity);
		basketRepository.save(basketEntity);
	}
  	
//(가은) 결제페이지 결제예정 상품리스트
	public List<BasketEntity> expectPay(List<Integer> basketIds) {
		List<BasketEntity> basketsToPay = basketRepository.findAllById(basketIds);
		return basketRepository.saveAll(basketsToPay);
	}

  //(가은) 결제정보(상품정보,결제수단) 주문테이블저장
	public void saveOrderInfo(List<Integer> basketIds, DeliveryEntity delivery,String tid) {
		for (Integer basketId : basketIds) {
			BasketEntity be = basketRepository.findById(basketId).get();
			OrderlistEntity ole = OrderlistEntity.builder().quantity(be.getQuantity())
																			.product(be.getProduct())
																			.delivery(delivery)
																			.orderTime(LocalDateTime.now())
																			.discount(be.getProduct().getDiscount())
																			.tid(tid)
																			.build();
			orderlistRepository.save(ole);
			sales(be.getProduct().getId(),be.getQuantity(),salesVolumeDesc(be.getProduct().getId()));
			// 장바구니에서 주문한 상품 제거
			basketRepository.deleteById(basketId);
		}
	}
	
//(가은)바로구매 장바구니 최근 담은 리스트 꺼내오기
	public List<BasketEntity> lastInBasket(Integer userId) {
		return basketRepository.findAllByUserIdOrderByIdDesc(userId);
	}
  	
  //(가은) 배송정보 저장
	public DeliveryEntity saveDelivery(DeliveryDTO delivery, String username, long point) {
		String completePay = "결제완료";
		UserEntity ue = userService.findByUserName(username);
		if(delivery.getMemo() == null) {
			delivery.setMemo("");
		}
		//운송장번호12자리
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 12; i++) {
			int digit = (int) (Math.random() * 10); // 0부터 9까지의 랜덤 숫자 생성
			sb.append(digit);
		}
		String waybillNum = sb.toString();
		DeliveryEntity de = DeliveryEntity.builder().memo(delivery.getMemo())
																					.receivePostcode(delivery.getReceivePostcode())
																					.receiveAddress(delivery.getReceiveAddress())
																					.receiveDetailAddress(delivery.getReceiveDetailAddress())
																					.receiveExtraAddress(delivery.getReceiveExtraAddress())
																					.receiveName(delivery.getReceiveName())
																					.receivePhone(delivery.getReceivePhone())
																					.waybill(waybillNum)
																					.situation(completePay)
																					.user(ue)
																					.usePoint(point)
																					.build();
		deliveryRepository.save(de);
		return de;
	}
  	
 //(가은)  orderId로 DeliveryEntity를 찾기
	public DeliveryEntity findDeliveryById(int deliveryId) {
		return deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new RuntimeException("해당 주문을 찾을 수 없습니다. 주문 ID: " + deliveryId));
				// DeliveryEntity를 찾지 못하면, 커스텀 메시지와 함께 RuntimeException을 던짐
	}
 	
 	
//(가은) 유저 주문내역 꺼내기
	public OrderlistEntity orders(int orderId){
		Optional<OrderlistEntity> oop = orderlistRepository.findById(orderId);
		return oop.get();
	}  	
  	
  	
//(가은) 찜 추가
	public void addToWish(Integer productId, String userName) {
		Optional<ProductEntity> pop = this.productRepository.findById(productId);
		Optional<UserEntity> uop = this.userRepository.findByUsername(userName);
		if(pop.isPresent()&&uop.isPresent()) {
			ProductEntity pe = pop.get();
			UserEntity ue = uop.get();
			if(pe.getWishUser().contains(ue)) {
				pe.getWishUser().remove(ue);
				ue.getWishList().remove(pe);
				userRepository.save(ue);
				productRepository.save(pe);
			}else {
				ue.getWishList().add(pe);
				pe.getWishUser().add(ue);
				userRepository.save(ue);
				productRepository.save(pe);
			}
		}
	}
	
//(가은) 찜 선택삭제
	public void removeSelectedWishlist(Integer userId, List<Integer> productIds) {
		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		List<ProductEntity> productsToRemove = productRepository.findAllById(productIds);
		user.getWishList().removeAll(productsToRemove);
		userRepository.save(user);
	}
	
//0808 성진 테스트
	public List<SalesVolumeEntity> salesVolumeDesc(Integer id) {
		//상품의 판매기록 검색
		List<SalesVolumeEntity> sopl = salesVolumeRepository.findByProductIdOrderByRecordDateDesc(id);
		if(sopl.isEmpty()) {
			sopl.add(SalesVolumeEntity.builder()
					.salesPrice(0).salesRate(0)
					.product(productRepository.findById(id).get())
					.build());
		}
		//없을경우 0을 리턴
		return sopl;
	}
	
	//0808 성진 수정
	public void sales(Integer id, int rate, List<SalesVolumeEntity> svel) {
		SalesVolumeEntity sve = null;
		ProductEntity pe = this.productRepository.findById(id).get();
		long sellprice = (long)(rate*pe.getSellPrice() * (1-pe.getDiscount()/100));
		// 원래 갯수가 0일경우 새로 db에 등록
		if(svel.get(0).getSalesRate() == 0) {
			sve = SalesVolumeEntity.builder()
					.product(pe)
					.recordDate(LocalDate.now())
					.salesRate(rate)
					.salesPrice(sellprice)
					.build();
		}else{
			//0이 아닐경우 날짜를 함께 검색하여 검색값이 있을경우 업데이트
			Optional<SalesVolumeEntity> sop = salesVolumeRepository.findByProductIdAndRecordDate(id, LocalDate.now());
			if(sop.isPresent()) {
				sve= sop.get();
				sve.setSalesRate(rate+sve.getSalesRate());
				sve.setSalesPrice(svel.get(0).getSalesPrice() + sellprice);
			}else {
				sve = SalesVolumeEntity.builder()
						.product(this.productRepository.findById(id).get())
						.recordDate(LocalDate.now())
						.salesRate(svel.get(0).getSalesRate() +rate)
						.salesPrice(svel.get(0).getSalesPrice() + sellprice)
						.build();
			}
		}
		pe.setStock(pe.getStock()-rate);
		productRepository.save(pe);
		AmountDTO amountDTO = adminService.total().get(0);
		amountDTO.setAmount(sellprice);
		amountDTO.setReason("sell");
		adminService.amount(amountDTO, LocalDate.now());
		salesVolumeRepository.save(sve);
	}
		
	//08 12 성진 테스트
	public List<ProducttypeEntity> getSortedProductTypes() {
		List<ProducttypeEntity> productTypes = producttypeRepository.findAll();
			for (ProducttypeEntity productType : productTypes) {
			List<ProductEntity> sortedProducts = productType.getProduct().stream()
				.sorted((product1, product2) -> {
					// Product 1의 최신 SalesVolumeEntity 가져오기
					Optional<SalesVolumeEntity> latestSalesVolume1 = product1.getSalesVolume().stream()
						.max(Comparator.comparing(SalesVolumeEntity::getRecordDate));
					// Product 2의 최신 SalesVolumeEntity 가져오기
					Optional<SalesVolumeEntity> latestSalesVolume2 = product2.getSalesVolume().stream()
						.max(Comparator.comparing(SalesVolumeEntity::getRecordDate));
						// Product 1과 Product 2의 SalesRate 추출
					Integer salesRate1 = latestSalesVolume1.map(SalesVolumeEntity::getSalesRate).orElse(Integer.MIN_VALUE);
					Integer salesRate2 = latestSalesVolume2.map(SalesVolumeEntity::getSalesRate).orElse(Integer.MIN_VALUE);
						// SalesRate에 따라 내림차순 정렬 (SalesRate가 없는 경우 가장 뒤로)
					return salesRate2.compareTo(salesRate1);
				})
				.collect(Collectors.toList());
				// 정렬된 제품 리스트를 다시 설정
			productType.setProduct(sortedProducts);
		}
		return productTypes;  // 정렬된 상태의 productTypes 반환
	}

	public DeliveryDTO DTOFindByTid(String tid) {
		List<OrderlistEntity> oll = orderlistRepository.findByTid(tid);
		if(!oll.isEmpty()) {
			return DeliveryDTO.entityToDTO(oll.get(0).getDelivery());
		}else {
			throw new RuntimeException("Order not found");
		}	
	}
	
	//08 13
	public void buyStock(Integer id, int rate) {
		ProductEntity pe = this.productRepository.findById(id).get();
		long buyprice = (long)(rate*pe.getBuyPrice());
		// 원래 갯수가 0일경우 새로 db에 등록
		pe.setStock(pe.getStock()+rate);
		productRepository.save(pe);
//		emailService.sendMailReject(ProductDTO.entityToDTO(pe), rate);
		AmountDTO amountDTO = adminService.total().get(0);
		amountDTO.setAmount(-buyprice);
		amountDTO.setReason("buy");
		adminService.amount(amountDTO, LocalDate.now());
	}

	@Scheduled(cron= "0 0 0 * * *")
	@Transactional
	public void deliveryReady() {
		List<DeliveryEntity> del = deliveryRepository.findAllBySituation("결제완료");
		if(!del.isEmpty()) {
			for(DeliveryEntity de : del) {
				if(LocalDateTime.now().isAfter(de.getOrder().get(0).getOrderTime().plusDays(1))){
					DeliveryEntity deli = deliveryRepository.findById(de.getId()).get();
					deli.setSituation("배송준비");
					deliveryRepository.save(deli);
				}
			}
		}
	}

	@Scheduled(cron= "0 0 0 * * *")
	@Transactional
	public void inDelivery() {
		List<DeliveryEntity> del = deliveryRepository.findAllBySituation("배송준비");
		if(!del.isEmpty()) {
			for(DeliveryEntity de : del) {
				if(LocalDateTime.now().isAfter(de.getOrder().get(0).getOrderTime().plusDays(2))){
					DeliveryEntity deli = deliveryRepository.findById(de.getId()).get();
					deli.setSituation("배송중");
					deliveryRepository.save(deli);
				}
			}
		}
	}
	
	@Scheduled(cron= "0 0 0 * * *")
	@Transactional
	public void deliveryComplete() {
		List<DeliveryEntity> del = deliveryRepository.findAllBySituation("배송중");
		if(!del.isEmpty()) {
			for(DeliveryEntity de : del) {
				if(LocalDateTime.now().isAfter(de.getOrder().get(0).getOrderTime().plusDays(4))){
					DeliveryEntity deli = deliveryRepository.findById(de.getId()).get();
					deli.setSituation("배송완료");
					deliveryRepository.save(deli);
				}
			}
		}
	}

	@Scheduled(cron= "0 0 0 * * *")
	@Transactional
	public void stockBuy() {
		List<ProductEntity> pel = productRepository.findAllByStockLessThanEqual(10);
		if(!pel.isEmpty()) {
			for(ProductEntity pe : pel) {
				buyStock(pe.getId(),50);
				pe.setStock(pe.getStock()+50);
				productRepository.save(pe);
			}
		}
	}
	
	
}
