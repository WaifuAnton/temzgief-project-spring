package com.shop.service;

import com.shop.config.constant.Constants;
import com.shop.entity.Clothes;
import com.shop.entity.Order;
import com.shop.entity.Product;
import com.shop.entity.User;
import com.shop.enumeration.Color;
import com.shop.enumeration.Status;
import com.shop.repository.OrderRepository;
import com.shop.repository.ClothesRepository;
import com.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.shop.repository.specification.ClothesSpecification.*;

@Service
public class ProductService {
    private ClothesRepository clothesRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    @Autowired
    public ProductService(ClothesRepository clothesRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.clothesRepository = clothesRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Page<Clothes> getClothesPage(String categoryName, String sortField, boolean descending, int page) {
        return descending
                ? clothesRepository.findAllByCategoryName(categoryName, PageRequest.of(page, Constants.PRODUCTS_PER_PAGE, Sort.by(sortField).descending()))
                : clothesRepository.findAllByCategoryName(categoryName, PageRequest.of(page, Constants.PRODUCTS_PER_PAGE, Sort.by(sortField).ascending()));
    }

    public Page<Clothes> getClothesPage(String categoryName, List<Color> colors, double from, double to, String sortField, boolean descending, int page) {
        Specification<Clothes> haveColors = null;
        if (colors == null || colors.size() == 0)
            colors = Arrays.asList(Color.values());
        else if (colors.size() == 1)
            haveColors = hasColor(colors.get(0));
        for (int i = 0; i < colors.size() - 1; i++)
            haveColors = hasColor(colors.get(i)).or(hasColor(colors.get(i + 1)));
        assert haveColors != null;
        Specification<Clothes> specification = haveColors.and(inCategory(categoryName)).and(priceBetween(from, to));
        return descending
                ? clothesRepository.findAll(specification, PageRequest.of(page, Constants.PRODUCTS_PER_PAGE, Sort.by(sortField).descending()))
                : clothesRepository.findAll(specification, PageRequest.of(page, Constants.PRODUCTS_PER_PAGE, Sort.by(sortField).ascending()));
    }

    public Page<Product> getClothesInCart(int page, String sortField, boolean descending) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> orders = orderRepository.getAllByUserIdAndStatus(currentUser.getId(), Status.CREATED);
        return descending
                ? productRepository.findAllByOrders(orders, PageRequest.of(page, Constants.PRODUCTS_PER_PAGE, Sort.by(sortField).descending()))
                : productRepository.findAllByOrders(orders, PageRequest.of(page, Constants.PRODUCTS_PER_PAGE, Sort.by(sortField).ascending()));
    }
}
