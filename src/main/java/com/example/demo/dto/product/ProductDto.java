package com.example.demo.dto.product;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.dto.AbstractDTO;
import com.example.demo.entity.product.Image;
import com.example.demo.entity.product.Product;

public class ProductDto extends AbstractDTO<ProductDto> {
	private Integer type;
	private String name;
	private String sku;
	private String slug;
	private String description;
	private Long price;
	private Long list_price;
	private String mainImage;
	private Integer weight;
	private Integer length;
	private Integer width;
	private Integer height;
	private List<String> images;
	private String category;
	private String subcategory;
	private String sizeWeight;
	private String material;
	private String features;





	// brand
	private String brand;

	// supplier
	private String supplier;




	public ProductDto() {
		super();
	}

	public ProductDto(Product entity) {
		super();
		this.setId(entity.getId());
		this.type = entity.getType();
		this.name = entity.getName();
		this.sku = entity.getSku();
		this.slug = entity.getSlug();
		this.description = entity.getDescription();
		this.price = entity.getPrice();
		this.list_price = entity.getList_price();
		this.mainImage = entity.getMainImage();
		this.sizeWeight = entity.getSizeWeight();
		this.material = entity.getMaterial();
		this.weight = entity.getWeight();
		this.length = entity.getLength();
		this.width = entity.getWidth();
		this.height = entity.getHeight();
		this.category = entity.getCategory().getCode();
		this.subcategory = entity.getSubcategory().getCode();
		this.brand = entity.getBrand().getCode();
		this.supplier = entity.getSupplier().getCode();
		this.features = entity.getFeatures();

		images = new ArrayList<>();
		for (Image image : entity.getImages()) {
			ImageDto dto = new ImageDto(image);
			images.add(dto.getUrl());
		}


	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getList_price() {
		return list_price;
	}

	public void setList_price(Long list_price) {
		this.list_price = list_price;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}


	public String getSizeWeight() {
		return sizeWeight;
	}

	public void setSizeWeight(String sizeWeight) {
		this.sizeWeight = sizeWeight;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}



}
