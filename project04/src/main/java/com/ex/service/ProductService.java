package com.ex.service;

import com.ex.data.BasketDTO;
import com.ex.data.ProductDTO;
import com.ex.entity.BasketEntity;
import com.ex.entity.ProductEntity;
import com.ex.entity.ProducttypeEntity;
import com.ex.entity.UserEntity;
import com.ex.repository.BasketRepository;
import com.ex.repository.ProductRepository;
import com.ex.repository.ProducttypeRepository;
import com.ex.repository.UserRepository;

import lombok.RequiredArgsConstructor;
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
    private final PhotoService photoService;

    public Integer create(ProductDTO productDTO, MultipartFile[] thumbnails, MultipartFile[] descriptionImages) throws IOException {
        Optional<ProducttypeEntity> optionalProductType = producttypeRepository.findById(productDTO.getTypeId());
        if (!optionalProductType.isPresent()) {
            throw new IllegalArgumentException("Invalid typeId");
        }
        ProducttypeEntity productType = optionalProductType.get();

        ProductEntity productEntity = ProductEntity.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .buyPrice(productDTO.getBuyPrice())
                .purchasePrice(productDTO.getPurchasePrice())
                .sellPrice(productDTO.getSellPrice())
                .discount(productDTO.getDiscount())
                .stock(productDTO.getStock())
                .type(productType)
                .registrationDate(LocalDateTime.now())
                .orderEmail(productDTO.getOrderEmail())
                .build();

        productRepository.save(productEntity);

        photoService.saveThumbnails(productEntity, thumbnails);
        photoService.saveDescriptionImages(productEntity, descriptionImages);

        return productEntity.getId();
    }

    public ProductDTO findById(Integer id) {
        Optional<ProductEntity> op = this.productRepository.findById(id);
        if(op.isPresent()) {
            ProductEntity pe = op.get();
            ProductDTO productDTO = ProductDTO.builder()
                    .id(pe.getId())
                    .type(pe.getType())
                    .name(pe.getName())
                    .description(pe.getDescription())
                    .discount(pe.getDiscount())
                    .buyPrice(pe.getBuyPrice())
                    .purchasePrice(pe.getPurchasePrice())
                    .sellPrice(pe.getSellPrice())
                    .stock(pe.getStock())
                    .registrationDate(pe.getRegistrationDate())
                    .orderEmail(pe.getOrderEmail())
                    .build();

            return productDTO;
        } else {
            throw new RuntimeException("product not found");
        }
    }

    public List<ProducttypeEntity> getAllProductTypes() {
        return producttypeRepository.findAll();
    }
    

  //장바구니 추가
  	public void addToBasket(Integer productId, String userName, int quantity) {
  		Optional<ProductEntity> pop = this.productRepository.findById(productId);
  		UserEntity ue = this.userService.findByUserName(userName);
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

//결제페이지 결제예정 상품
  	public List<ProductEntity> expectPay(String selectProduct) {
  		 String[] productIds = selectProduct.split(",");//선택된 상품의 ID를 배열로		    
		    List<ProductEntity> selectedProducts = new ArrayList<>();// 선택된 상품들을 저장할 리스트
		    for (String id : productIds) {// 각 ID에 해당하는 상품을 찾아서 리스트에 추가
		        Optional<ProductEntity> op = this.productRepository.findById(Integer.parseInt(id));
		        selectedProducts.add(op.get());		        
		    }
		    return selectedProducts;
  	}
  	
//주문내역(결제) 넣기 미완성
  	public void addToOrderList(Integer productId, String userName) {
  		Optional<ProductEntity> pop = this.productRepository.findById(productId);
  		UserEntity ue = this.userService.findByUserName(userName);
  	}
  	
//모든상품 조회 리스트
  	public List<ProductEntity> allProduct() {
  		return productRepository.findAll();
  	}

  	
  	
}
