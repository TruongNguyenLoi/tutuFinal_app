package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.inventory.Inventory;
import com.example.demo.entity.product.Product;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	public Boolean existsByProduct(Product p);

	public List<Inventory> getAllByProductId(Long productId);
	
	public Inventory getOneByProduct(Product p);
	
	
	//SELECT count(*) from tbl_inventory i where i.total_item = 0
	@Query("SELECT count(*) from Inventory i where i.quantity_item = 0")
	public Integer getTotalItemOutOfStock();	// lấy số lượng sản phẩm
	
}
