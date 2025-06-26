package Project_ITSS.common.service.impl;

import Project_ITSS.common.entity.Product;
import Project_ITSS.common.repository.IProductRepository;
import Project_ITSS.common.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("mainProductService")
@Primary
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(IProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public int addProduct(Product product) {
        return productRepository.insertProductInfo(product);
    }

    @Override
    public void updateProduct(Product product) {
        productRepository.updateProduct(product);
    }

    @Override
    public Optional<Product> getProductById(long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByType(String type) {
        return productRepository.findByType(type);
    }

    @Override
    public boolean checkProductValidity(int quantity, long productId) {
        return productRepository.checkProductValidity(quantity, productId);
    }

    @Override
    public void deleteProduct(long productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public List<Product> searchProductsByTitle(String title) {
        return productRepository.findByTitleContaining(title);
    }
}