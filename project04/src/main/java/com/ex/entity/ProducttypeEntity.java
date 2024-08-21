package com.ex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ProducttypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String typename;

	@OneToMany(mappedBy = "type", cascade = CascadeType.REMOVE)
	@ToString.Exclude
	private List<ProductEntity> product;
	
	private String content;
}
