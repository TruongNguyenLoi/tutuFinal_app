package com.example.demo.dto.product;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.common.CalculateDiscount;
import com.example.demo.dto.AbstractDTO;
import com.example.demo.dto.category.CategoryDtoRes;
import com.example.demo.dto.category.SubcategoryDtoRes;
import com.example.demo.dto.inventory.InventoryProductDto;
import com.example.demo.entity.category.Category;
import com.example.demo.entity.category.SubCategory;
import com.example.demo.entity.inventory.Inventory;
import com.example.demo.entity.product.Brand;
import com.example.demo.entity.product.Image;
import com.example.demo.entity.product.Product;
import com.example.demo.entity.promotion.ProductDiscount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ProductDtoRes extends AbstractDTO<ProductDtoRes> {

	private Integer type; // loai san phamr

	@JsonInclude(value = Include.NON_NULL)
	private String name;

	@JsonInclude(value = Include.NON_NULL)
	private String sku;

	@JsonInclude(value = Include.NON_NULL)
	private String slug;

	@JsonInclude(value = Include.NON_NULL)
	private String description;

	@JsonInclude(value = Include.NON_NULL)
	private Long price;

	@JsonInclude(value = Include.NON_NULL)
	private Long list_price;

	@JsonInclude(value = Include.NON_NULL)
	private Double percent_discount;

	@JsonInclude(value = Include.NON_NULL)
	private String mainImage;

	@JsonInclude(value = Include.NON_NULL)
	private String features;

	private Integer weight;
	private Integer length;
	private Integer width;
	private Integer height;

	@JsonInclude(value = Include.NON_NULL)
	private Integer in_stock;

	@JsonInclude(value = Include.NON_NULL)
	private Integer seller_count;

	@JsonInclude(value = Include.NON_NULL)
	private Integer review_count;

	@JsonInclude(value = Include.NON_EMPTY)
	private List<String> images;

	@JsonInclude(value = Include.NON_EMPTY)
	private List<String> tags;

	@JsonInclude(value = Include.NON_NULL)
	private CategoryDtoRes category;

	@JsonInclude(value = Include.NON_NULL)
	private SubcategoryDtoRes subcategory;

	@JsonInclude(value = Include.NON_EMPTY)
	private List<ProductSpecify> product_specs;

	// brand
	@JsonInclude(value = Include.NON_NULL)
	private BrandDtoRes brand;


	@JsonInclude(value = Include.NON_NULL)
	private List<InventoryProductDto> inventories;

	public ProductDtoRes() {
		// TODO Auto-generated constructor stub
	}

	public ProductDtoRes(Product entity) {
		super();
		this.setId(entity.getId());
		this.type = entity.getType();
		this.name = entity.getName();
		this.sku = entity.getSku();
		this.slug = entity.getSlug();
		this.description = entity.getDescription();
		this.price = entity.getPrice();
		this.list_price = entity.getList_price();
		this.features = entity.getFeatures();
		ProductDiscount discount = entity.getDiscount();
		if (discount.getStatus() == 1) {
			if (discount.getType() != null && discount.getValue() != null) {
				if (discount.getType() == 1) {
					this.price = entity.getPrice() * (100 - discount.getValue()) / 100;
				} else {
					this.price = entity.getPrice() - discount.getValue();
				}
			} else {
				this.price = entity.getPrice();
			}
		} else {
			this.price = entity.getPrice();
		}
		if (this.price != null && this.list_price != null) {
			this.percent_discount = CalculateDiscount.countDiscount(this.price, this.list_price);
		} else {
			this.percent_discount = null;
		}

		this.mainImage = entity.getMainIamge();
		this.weight = entity.getWeight();
		this.length = entity.getLength();
		this.width = entity.getWidth();
		this.height = entity.getHeight();

		category = new CategoryDtoRes();
		if (category != null) {
			Category item = entity.getCategory();
			this.category = new CategoryDtoRes(item);
		}
		subcategory = new SubcategoryDtoRes();
		if (subcategory != null) {
			SubCategory item = entity.getSubcategory();
			this.subcategory = new SubcategoryDtoRes(item);
		}

		brand = new BrandDtoRes();
		if (brand != null) {
			Brand brandEntity = entity.getBrand();
			brand = new BrandDtoRes(brandEntity);
		}

		images = new ArrayList<>();
		for (Image image : entity.getImages()) {
			ImageDto dto = new ImageDto(image);
			images.add(dto.getUrl());
		}
		images.add(0, this.mainImage);
//
//		tags = new ArrayList<>();
//		for (Tag tag : entity.getTags()) {
//			String item = tag.getCode();
//			tags.add(item);
//		}

		this.product_specs = new ArrayList<>();
		if (entity.getSizeWeight() != null) {
			this.product_specs.add(new ProductSpecify("Trọng lượng & Kích thước", entity.getSizeWeight()));
		}
		if (entity.getMaterial() != null) {
			this.product_specs.add(new ProductSpecify("Thành phần", entity.getMaterial()));
		}


		this.inventories = new ArrayList<>();
		if (inventories != null) {
			List<Inventory> invs = entity.getInventories();
			for (Inventory item : invs) {
				InventoryProductDto dto = new InventoryProductDto(item);
				this.inventories.add(dto);
			}
		}
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

	public Double getPercent_discount() {
		return percent_discount;
	}

	public void setPercent_discount(Double percent_discount) {
		this.percent_discount = percent_discount;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
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

	public CategoryDtoRes getCategory() {
		return category;
	}

	public void setCategory(CategoryDtoRes category) {
		this.category = category;
	}

	public SubcategoryDtoRes getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(SubcategoryDtoRes subcategory) {
		this.subcategory = subcategory;
	}



	public BrandDtoRes getBrand() {
		return brand;
	}

	public void setBrand(BrandDtoRes brand) {
		this.brand = brand;
	}

	public Integer getIn_stock() {
		return in_stock;
	}

	public void setIn_stock(Integer in_stock) {
		this.in_stock = in_stock;
	}

	public Integer getSeller_count() {
		return seller_count;
	}

	public void setSeller_count(Integer seller_count) {
		this.seller_count = seller_count;
	}

	public Integer getReview_count() {
		return review_count;
	}

	public void setReview_count(Integer review_count) {
		this.review_count = review_count;
	}

	public List<ProductSpecify> getProduct_specs() {
		return product_specs;
	}

	public void setProduct_specs(List<ProductSpecify> product_specs) {
		this.product_specs = product_specs;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<InventoryProductDto> getInventories() {
		return inventories;
	}

	public void setInventories(List<InventoryProductDto> inventories) {
		this.inventories = inventories;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

}
