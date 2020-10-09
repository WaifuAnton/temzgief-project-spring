package com.shop.controller;

import com.shop.entity.Product;
import com.shop.service.OrderService;
import com.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class CartController {
    private OrderService orderService;
    private ProductService productService;

    @Autowired
    public CartController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping("/add/{productId}")
    public String add(@PathVariable long productId) {
        orderService.addProductToCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String remove(@PathVariable long productId) {
        orderService.removeProductFromCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/submit")
    public String submit() {
        orderService.submitOrdersInCart();
        return "redirect:/categories/all";
    }

    @GetMapping
    public String cart(Model model,
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
                       @RequestParam(name = "descending", defaultValue = "false") boolean descending) {
        Page<Product> products = productService.getClothesInCart(page - 1, sortBy, descending);
        model.addAttribute("productsInCart", products);
        model.addAttribute("products", products);
        int totalPages = products.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("descending", descending);
        return "cart";
    }
}
