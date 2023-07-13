package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.example.demo.dto.AdvanceSearchDto;
import com.example.demo.dto.SearchDto;
import com.example.demo.dto.product.ProductDto;
import com.example.demo.dto.product.ProductDtoRes;
import com.example.demo.dto.product.ProductListDto;
import com.example.demo.dto.product.ProductTopSale;

@Service
public interface ProductService {
	public List<ProductListDto> getAll();

	// Lấy các sản phẩm hiển thị lên trang chủ, có trạng thái hiển thị là 1
//	public Page<ProductDto> searchByPage(SearchDto dto);
	
	// Lấy các sản phẩm hiển thị lên trang chủ, có trạng thái hiển thị là 1
	public Page<ProductListDto> productList(SearchDto dto);
	
	// lấy toàn bộ sản phẩm trong csdl
	public Page<ProductListDto> getAllProduct(AdvanceSearchDto dto);
	
	// lấy toàn bộ sản phẩm có trạng thái 1 theo thương hiệu
	public List<ProductListDto> getAllByBrand(Long productId, String brandCode);
	
	// lấy toàn bộ sản phẩm có trạng thái 1 theo thương hiệu, phân trang
	public Page<ProductListDto> getAllProductByBrand(String brandCode, Integer page, Integer limit, String sortBy);
	
	// lấy thông tin sản phẩm bán chạy
	public Page<ProductTopSale> topSaleProduct(SearchDto dto);
	
	// lấy thông tin sản phẩm theo id
	public ProductDtoRes getProductById(Long id);
	
	// lấy thông tin sản phẩm theo id
	public ProductDto getDetailProduct(Long id);

	// Them hoặc cập nhật san pham
	public ProductDto saveOrUpdate(ProductDto dto);
	
	// xoá mềm sản phẩm
	public Boolean delete(Long id);

}
