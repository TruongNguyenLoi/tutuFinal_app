package com.example.demo.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.demo.common.CalculateDiscount;
import com.example.demo.common.Slug;
import com.example.demo.dto.AdvanceSearchDto;
import com.example.demo.dto.SearchDto;
import com.example.demo.dto.product.ProductDto;
import com.example.demo.dto.product.ProductDtoRes;
import com.example.demo.dto.product.ProductListDto;
import com.example.demo.dto.product.ProductTopSale;
import com.example.demo.entity.category.Category;
import com.example.demo.entity.category.SubCategory;
import com.example.demo.entity.inventory.Inventory;
import com.example.demo.entity.inventory.Supplier;
import com.example.demo.entity.product.Brand;
import com.example.demo.entity.product.Image;
import com.example.demo.entity.product.Product;
import com.example.demo.entity.promotion.ProductDiscount;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ImageRepository;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.ProductDiscountRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SubCategoryRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private EntityManager manager;

	@Autowired
	private ProductRepository productRepos;

	@Autowired
	private CategoryRepository categoryRepos;

	@Autowired
	private SubCategoryRepository subcategoryRepos;

	@Autowired
	private ImageRepository imageRepos;

	@Autowired
	private BrandRepository brandRepos;
	
	@Autowired
	private SupplierRepository supplierRepos;


	@Autowired
	private InventoryRepository inventoryRepos;

	@Autowired
	private OrderDetailRepository orderDetailRepos;
	

	
	@Autowired
	private ProductDiscountRepository discountRepos;
	
//	@Autowired
//	private TagRepository tagRepos;
	@Override
	public List<ProductListDto> getAll(){
		List<Product> list = productRepos.findAll();
		List<ProductListDto> dtos = new ArrayList<>();

		for (Product p : list) {
				ProductListDto dto = new ProductListDto(p);
				dtos.add(dto);
		}
		return  dtos;
	}
	@Override
	public Page<ProductListDto> productList(SearchDto dto) {
		int pageIndex = dto.getPageIndex();
		int pageSize = dto.getPageSize();
		if (pageIndex > 0)
			pageIndex -= 1;
		else
			pageIndex = 0;

		String whereClause = "";
		String orderBy = " ORDER BY entity." + dto.getSortBy() + " " + dto.getSortValue();
		String sqlCount = "select count(entity.id) from  Product as entity where (1=1) ";
		String sql = "select new com.example.demo.dto.product.ProductListDto(entity) from  Product as entity where entity.display=1 AND (1=1)  ";
		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			
			if(dto.getKeyword().contains(" ")) {
				String[] keywords = dto.getKeyword().split(" ");
				whereClause += " AND ( entity.name LIKE " + "'" + keywords[0] + "'" + " OR entity.description LIKE "+ "'" + keywords[0]+ "'"
						+ " OR entity.slug LIKE "+ "'" + keywords[0]+ "'" + " OR entity.category.name LIKE "+ "'" + keywords[0]+ "'" + " OR entity.category.code LIKE " + "'"+ keywords[0]+ "'"
						+ " OR entity.subcategory.name LIKE " + "'"+ keywords[0] + "'"+ " OR entity.subcategory.code LIKE " + "'"+ keywords[0]+ "'";
				for(int i = 1; i < keywords.length; i++) {
					whereClause += " or entity.name LIKE " + "'" +  keywords[i]+ "'" + " OR entity.description LIKE "+ "'" + keywords[i]+ "'"
							+ " OR entity.slug LIKE "+ "'" + keywords[i]+ "'" + " OR entity.category.name LIKE "+ "'" + keywords[i]+ "'" + " OR entity.category.code LIKE "+ "'" + keywords[i]+ "'"
							+ " OR entity.subcategory.name LIKE "+ "'" + keywords[i]+ "'" + " OR entity.subcategory.code LIKE "+ "'" + keywords[i]+ "'" ;
				}
				whereClause += " ) ";
			} else {
				whereClause += " AND ( entity.name LIKE :text " + "OR entity.description LIKE :text "
						+ "OR entity.slug LIKE :text " + "OR entity.slug LIKE :text "
						+ "OR entity.category.name LIKE :text " + "OR entity.category.code LIKE :text "
						+ "OR entity.subcategory.name LIKE :text " + "OR entity.subcategory.code LIKE :text )";
			}
			
		}

		if (dto.getCategory() != null) {
			whereClause += " AND ( entity.category.code LIKE :category )";
		}
		if (dto.getSubcategory() != null) {
			whereClause += " AND ( entity.subcategory.code LIKE :subcategory )";
		}

		if (dto.getBrand() != null && StringUtils.hasText(dto.getBrand())) {
			if (dto.getBrand().contains(",")) {
				String[] s = dto.getBrand().split(",");
				whereClause += " AND ( entity.brand.code = '" + s[0] + "' )";
				for (int i = 1; i < s.length; i++) {
					whereClause += " OR ( entity.brand.code = '" + s[i] + "' )";
				}
			} else {
				whereClause += " AND ( entity.brand.code = :brand )";
			}
		} else {
			whereClause += "";
		}

		if (dto.getPrice() != null && !dto.getPrice().equalsIgnoreCase("")) {
			String[] s = dto.getPrice().toString().split(",");
			Long begin = Long.parseLong(s[0]);
			Long end = Long.parseLong(s[1]);
			whereClause += " AND ( entity.price BETWEEN " + begin + " AND " + end + " )";
		} else {
			whereClause += "";
		}

		sql += whereClause + orderBy;
		sqlCount += whereClause;
//		System.out.println(sql);

		Query q = manager.createQuery(sql, ProductListDto.class);
		Query qCount = manager.createQuery(sqlCount);

		if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
			if(dto.getKeyword().contains(" ")) {
				
			} else {
				q.setParameter("text", '%' + dto.getKeyword() + '%');
				qCount.setParameter("text", '%' + dto.getKeyword() + '%');
			}
			
		}

		if (dto.getCategory() != null) {
			q.setParameter("category", dto.getCategory());
			qCount.setParameter("category", dto.getCategory());
		}

		if (dto.getSubcategory() != null) {
			q.setParameter("subcategory", dto.getSubcategory());
			qCount.setParameter("subcategory", dto.getSubcategory());
		}

		if (dto.getImage() != null && StringUtils.hasText(dto.getImage())) {
			q.setParameter("image", dto.getImage());
			qCount.setParameter("image", dto.getImage());
		}
		if (dto.getBrand() != null && dto.getBrand().length() > 0 && dto.getBrand().contains(",") == false) {
			q.setParameter("brand", dto.getBrand());
			qCount.setParameter("brand", dto.getBrand());
		}

		int startPosition = pageIndex * pageSize;
		q.setFirstResult(startPosition);
		q.setMaxResults(pageSize);

		@SuppressWarnings("unchecked")
		List<ProductListDto> entities = q.getResultList();
		Integer seller_count = 0;
		for (ProductListDto item : entities) {
			if (orderDetailRepos.countAllByProductId(item.getId()) != null) {
				seller_count += orderDetailRepos.countAllByProductId(item.getId());
			} else {
				seller_count = 0;
			}
			item.setSeller_count(seller_count);
			seller_count = 0;
		}

		long count = (long) qCount.getSingleResult();
		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		Page<ProductListDto> result = new PageImpl<ProductListDto>(entities, pageable, count);
		return result;
	}

	@Override
	public Page<ProductListDto> getAllProduct(AdvanceSearchDto dto) {
		int pageIndex = dto.getPageIndex();
		int pageSize = dto.getPageSize();
		if (pageIndex > 0)
			pageIndex -= 1;
		else
			pageIndex = 0;

		String whereClause = "";
		String orderBy = " ORDER BY entity.id DESC";
		String sqlCount = "select count(entity.id) from  Product as entity where (1=1) ";
		String sql = "select new com.example.demo.dto.product.ProductListDto(entity) from  Product as entity where (1=1) ";

		if (dto.getDisplay() == 0 || dto.getDisplay() == 1) {
			whereClause += " AND ( entity.display = " + dto.getDisplay() + ")";
		} else {
			whereClause += "";
		}

		if (dto.getName() != null && StringUtils.hasText(dto.getName())) {
			whereClause += " AND ( entity.name LIKE :name " + "OR entity.description LIKE :name "
					+ "OR entity.slug LIKE :name )";
		} else {
			whereClause += "";
		}

		if (dto.getSku() != null && StringUtils.hasText(dto.getSku())) {
			whereClause += " AND ( entity.sku LIKE :sku )";
		}

		if (dto.getCategory() != null && StringUtils.hasText(dto.getCategory())) {
			whereClause += " AND ( entity.category.code = :category )";
		} else {
			whereClause += "";
		}

		if (dto.getBrand() != null && StringUtils.hasText(dto.getBrand())) {
			if (dto.getBrand().contains(",")) {
				String[] s = dto.getBrand().split(",");
				whereClause += " AND ( entity.brand.code = '" + s[0] + "' )";
				for (int i = 1; i < s.length; i++) {
					whereClause += " OR ( entity.brand.code = '" + s[i] + "' )";
				}
			} else {
				whereClause += " AND ( entity.brand.code = :brand )";
			}
		} else {
			whereClause += "";
		}
		
		if (dto.getSupplier() != null && StringUtils.hasText(dto.getSupplier())) {
			if (dto.getSupplier().contains(",")) {
				String[] s = dto.getSupplier().split(",");
				whereClause += " AND ( entity.supplier.code = '" + s[0] + "' )";
				for (int i = 1; i < s.length; i++) {
					whereClause += " OR ( entity.supplier.code = '" + s[i] + "' )";
				}
			} else {
				whereClause += " AND ( entity.supplier.code = :supplier )";
			}
		} else {
			whereClause += "";
		}

		sql += whereClause + orderBy;
		sqlCount += whereClause;

		Query q = manager.createQuery(sql, ProductListDto.class);
		Query qCount = manager.createQuery(sqlCount);

		if (dto.getName() != null && StringUtils.hasText(dto.getName())) {
			q.setParameter("name", '%' + dto.getName() + '%');
			qCount.setParameter("name", '%' + dto.getName() + '%');
		}

		if (dto.getSku() != null && StringUtils.hasText(dto.getSku())) {
			q.setParameter("sku", '%' + dto.getSku() + '%');
			qCount.setParameter("sku", '%' + dto.getSku() + '%');
		}

		if (dto.getCategory() != null && dto.getCategory().length() > 0) {
			q.setParameter("category", dto.getCategory());
			qCount.setParameter("category", dto.getCategory());
		}

		if (dto.getBrand() != null && dto.getBrand().length() > 0 && !dto.getBrand().contains(",")) {
			q.setParameter("brand", dto.getBrand());
			qCount.setParameter("brand", dto.getBrand());
		}
		
		if (dto.getSupplier() != null && dto.getSupplier().length() > 0 && !dto.getSupplier().contains(",")) {
			q.setParameter("supplier", dto.getSupplier());
			qCount.setParameter("supplier", dto.getSupplier());
		}

		int startPosition = pageIndex * pageSize;
		q.setFirstResult(startPosition);
		q.setMaxResults(pageSize);

		@SuppressWarnings("unchecked")
		List<ProductListDto> entities = q.getResultList();
		Integer seller_count = 0;
		for (ProductListDto item : entities) {
			if (orderDetailRepos.countAllByProductId(item.getId()) != null) {
				seller_count = orderDetailRepos.countAllByProductId(item.getId());
			} else {
				seller_count = 0;
			}
			item.setSeller_count(seller_count);
		}

		long count = (long) qCount.getSingleResult();
		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		Page<ProductListDto> result = new PageImpl<ProductListDto>(entities, pageable, count);
		return result;
	}

	@Override
	public ProductDto saveOrUpdate(ProductDto dto) {
		if (dto != null) {

			Product entity = null;
			Image image = null;
			Inventory inventory = null;
			ProductDiscount discount = null;
//			Tag tag = null;

			Category category = categoryRepos.findOneByCode(dto.getCategory());
			SubCategory subcategory = subcategoryRepos.findOneByCode(dto.getSubcategory());
			Brand brand = brandRepos.findOneByCode(dto.getBrand());
			Supplier supplier = supplierRepos.findOneByCode(dto.getSupplier());
			// 1 - n product - image
			List<String> imageUrls = dto.getImages();
			List<Image> images = new ArrayList<>();
			

			

			
			List<Inventory> inventories = new ArrayList<>();

			if (dto.getId() != null) {
				entity = productRepos.getById(dto.getId());
				entity.setUpdatedDate(new Timestamp(new Date().getTime()).toString());

				List<Image> imagesProduct = imageRepos.findAllByProductId(entity.getId());
				for (Image item : imagesProduct) {
					imageRepos.deleteByProductId(item.getProduct().getId());
				}

				for (int i = 0; i < imageUrls.size(); i++) {
					image = new Image(imageUrls.get(i));
					images.add(image);
				}
				discount = discountRepos.findOneByProduct(entity);

			}
			if (entity == null) {
				entity = new Product();
				entity.setCreatedDate(new Timestamp(new Date().getTime()).toString());
				entity.setUpdatedDate(new Timestamp(new Date().getTime()).toString());

				discount = new ProductDiscount();
				discount.setStatus(1);
				discount.setProduct(entity);
//					inventory = new Inventory(0, 0, entity, item, category.getCode());
					inventory = new Inventory();
					inventory.setDisplay(1);
					inventory.setCategory_code(category.getCode());
					inventory.setQuantity_item(0);
					inventory.setTotal_import_item(0);
					inventory.setProduct(entity);
					inventories.add(inventory);

				for (int i = 0; i < imageUrls.size(); i++) {
					image = new Image(imageUrls.get(i));
					images.add(image);
				}
			}
			String features = dto.getName()+","+dto.getCategory()+","+dto.getSubcategory()+","+dto.getSupplier()+","+dto.getBrand();
			entity.setType(dto.getType());
			entity.setName(dto.getName());
			entity.setMainImage(dto.getMainImage());
			entity.setWeight(dto.getWeight());
			entity.setLength(dto.getLength());
			entity.setWidth(dto.getWidth());
			entity.setHeight(dto.getHeight());
			entity.setPrice(dto.getPrice());
			entity.setList_price(dto.getList_price());
			entity.setSku(dto.getSku());
			entity.setSlug(Slug.makeSlug(dto.getName()));
			entity.setDescription(dto.getDescription());
			entity.setSizeWeight(dto.getSizeWeight());
			entity.setMaterial(dto.getMaterial());
			entity.setFeatures(features);
			entity.setDisplay(1);
			entity.setCategory(category);
			entity.setSubcategory(subcategory);
			entity.setBrand(brand);
			entity.setSupplier(supplier);
			entity.setDiscount(discount);
			entity.setImages(images);
			entity.setInventories(inventories);
			for (int i = 0; i < images.size(); i++) {
				images.get(i).setProduct(entity);
			}
			
			entity = productRepos.save(entity);//
			entity.setDiscount(discount);
			discount.setProduct(entity);

		}
		return null;
	}

	@Override
	public Boolean delete(Long id) {
		if (id != null) {
			Product entity = productRepos.getById(id);
			if (entity.getDisplay() == 1) {
				entity.setDisplay(0);
			} else {
				entity.setDisplay(1);
			}
			productRepos.save(entity);
			return true;
		}
		return false;
	}

	@Override
	public ProductDtoRes getProductById(Long id) {
		Product product = productRepos.getById(id);


		Inventory inv = inventoryRepos.getOneByProduct(product);
		ProductDtoRes dto = new ProductDtoRes(product);
		dto.setIn_stock(inv.getQuantity_item());
		return dto;
	}

	@Override
	public ProductDto getDetailProduct(Long id) {
		Product product = productRepos.getById(id);
		ProductDto dto = new ProductDto(product);
//		List<String> colors = new ArrayList<>();
//		for(Inventory item : product.getInventories()) {
//			colors.add(item.getColor().getName());
//		}
//		dto.setColors(colors);
		return dto;
	}

	@Override
	public Page<ProductListDto> getAllProductByBrand(String brandCode, Integer page, Integer limit, String sortBy) {
		Brand brand = brandRepos.findOneByCode(brandCode);
		Page<Product> list = productRepos.findAllByBrand(brand,
				PageRequest.of(page, limit, Sort.by(sortBy).descending()));
		Page<ProductListDto> dtos = list.map(item -> new ProductListDto(item));
		return dtos;
	}

	@Override
	public List<ProductListDto> getAllByBrand(Long productId, String brandCode) {
		Brand brand = brandRepos.findOneByCode(brandCode);
		Page<Product> pages = productRepos.findAllByBrand(brand, PageRequest.of(0, 4, Sort.by("id").descending()));
		List<Product> list = pages.getContent();
		List<ProductListDto> dtos = new ArrayList<>();
		for (Product p : list) {
			if (!Objects.equals(productId, p.getId())) {
				ProductListDto dto = new ProductListDto(p);
				dtos.add(dto);
			}
		}
		Integer seller_count = 0;
		for (ProductListDto item : dtos) {
			if (orderDetailRepos.countAllByProductId(item.getId()) != null) {
				seller_count += orderDetailRepos.countAllByProductId(item.getId());
			} else {
				seller_count = 0;
			}
			item.setSeller_count(seller_count);
			seller_count = 0;
		}
		return dtos;
	}

	@Override
	public Page<ProductTopSale> topSaleProduct(SearchDto dto) {
		int pageIndex = dto.getPageIndex();
		int pageSize = dto.getPageSize();
		if (pageIndex > 0)
			pageIndex -= 1;
		else
			pageIndex = 0;
//		String groupOrderClause = " GROUP BY s.product.id ORDER BY quantity_sold DESC";
//		String sqlCount = "select count(*) from OrderDetail as s " + "INNER JOIN Product p ON s.product.id = p.id "
//				+ " INNER JOIN Order o ON o.status = 2 and o.id = s.order.id " + "GROUP BY s.product.id ";
//		String sql = "select new com.example.demo.dto.product.ProductTopSale(p.id as id, p.name as name, "
//				+ "p.slug as slug, p.price as price, p.list_price as list_price, p.mainImage as mainImage, p.brand.name as brandName, p.brand.madeIn as brandMadeIn, "
//				+ " SUM(s.quantity) as quantity_sold) " + " from OrderDetail as s "
//				+ " INNER JOIN Product p ON s.product.id = p.id"
//				+ " INNER JOIN Order o ON o.status = 2 and o.id = s.order.id "
//				+ " AND (TIMESTAMPDIFF(DAY, o.createdDate, NOW()) <= 30 )";
		String groupOrderClause = " GROUP BY s.product.id ORDER BY quantity_sold DESC";
		String sqlCount = "select count(*) from OrderDetail as s " + "INNER JOIN Product p ON s.product.id = p.id "
				+ " INNER JOIN Order o ON o.status = 3 and o.id = s.order.id " + "GROUP BY s.product.id ";
		String sql = "select new com.example.demo.dto.product.ProductTopSale(p.id as id, p.name as name, "
				+ "p.slug as slug, p.price as price, p.list_price as list_price, p.mainImage as mainImage, p.brand.name as brandName, p.brand.madeIn as brandMadeIn, "
				+ " SUM(s.quantity) as quantity_sold) " + " from OrderDetail as s "
				+ " INNER JOIN Product p ON s.product.id = p.id"
				+ " INNER JOIN Order o ON o.status = 3 and o.id = s.order.id "
				+ " AND (TIMESTAMPDIFF(DAY, o.createdDate, NOW()) <= 30 )";
		sql += groupOrderClause;

		Query q = manager.createQuery(sql, ProductTopSale.class);
		Query qCount = manager.createQuery(sqlCount);
		q.setMaxResults(pageSize);

		int startPosition = pageIndex * pageSize;
		q.setFirstResult(startPosition);
		q.setMaxResults(pageSize);
		long count = (long) qCount.getResultList().size();

		@SuppressWarnings("unchecked")
		List<ProductTopSale> entities = q.getResultList();
		
		for(ProductTopSale item : entities) {
			if (item.getPrice() != null && item.getList_price() != null) {
				item.setPercent_discount(CalculateDiscount.countDiscount(item.getPrice(), item.getList_price()));
			} else {
				item.setPercent_discount(null);
			}
		}

		Pageable pageable = PageRequest.of(pageIndex, pageSize);

		Page<ProductTopSale> result = new PageImpl<ProductTopSale>(entities, pageable, count);
		return result;
	}

}
