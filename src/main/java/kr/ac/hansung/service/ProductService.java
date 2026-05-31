package kr.ac.hansung.service;

import kr.ac.hansung.dto.PageResponseDto;
import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다: " + id));
    }

    @Transactional
    public Product save(ProductDto dto) {
        Product product = new Product(
            dto.getName(), dto.getPrice(), dto.getDescription(), dto.getStock()
        );
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public Product updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        return product;  // 더티 체킹으로 자동 저장
    }

    @Transactional(readOnly = true)
    public PageResponseDto<Product> findWithPageInfo(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<Product> productPage = productRepository.findAll(pageable);
        return PageResponseDto.of(productPage);
    }

    @Transactional(readOnly = true)
    public PageResponseDto<Product> searchProducts(String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<Product> productPage = productRepository.findByNameContaining(keyword, pageable);
        return PageResponseDto.of(productPage);
    }
}
