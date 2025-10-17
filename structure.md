src/main/java/com/shopacc
├── ShopaccApplication.java  # Main class (@EnableCaching, @EnableJpaRepositories)
├── config
│   ├── JpaConfig.java
│   ├── SecurityConfig.java
│   ├── MyBatisConfig.java  # Bổ sung cho MyBatis
│   ├── ThymeleafConfig.java  # Nếu cần custom resolver
│   ├── CacheConfig.java    # @EnableCaching + config caches
│   └── MailConfig.java     # Spring Mail config
├── controller
│   ├── AuthController.java  # Login/register + email xác thực
│   ├── StaffController.java
│   ├── CustomerController.java
│   ├── AccountController.java
│   ├── OrderController.java
│   ├── WishlistController.java
│   ├── CartController.java
│   ├── TransactionController.java
│   ├── PaymentController.java
│   └── DashboardController.java  # Báo cáo (dùng cache)
├── dto  # Bổ sung DTO nếu cần form/validation
│   ├── CustomerForm.java   # Với @Valid annotations
│   └── ...
├── enums
│   ├── AccountStatus.java
│   ├── OrderStatus.java
│   ├── Role.java
│   └── PaymentMethodsEnum.java
├── exception
│   ├── AppException.java
│   ├── ErrorCode.java
│   └── GlobalExceptionHandler.java  # @ControllerAdvice
├── filter
│   └── RequestLoggingFilter.java
├── mapper  # MyBatis XML mappers
│   ├── AccountMapper.xml
│   └── ... (cho query phức tạp)
├── model
│   ├── BaseEntity.java  # Timestamps
│   ├── Staff.java
│   ├── Customer.java
│   ├── Account.java
│   ├── Wishlist.java
│   ├── Cart.java
│   ├── Order.java
│   ├── OrderDetail.java
│   ├── PaymentMethod.java
│   ├── Transaction.java
│   └── Payment.java
├── repository
│   ├── StaffRepository.java  # JpaRepository
│   ├── ... (tương tự cho các entity)
│   └── custom  # Nếu cần custom JPA
│       └── AccountCustomRepository.java
├── service
│   ├── StaffService.java
│   ├── impl
│       ├── StaffServiceImpl.java
│       └── ... (tương tự)
│   └── mybatis  # Bổ sung cho MyBatis service
│       └── RevenueService.java  # Query phức tạp + @Cacheable
└── utils
├── JwtUtil.java  # Nếu dùng JWT
├── EmailUtil.java  # Gửi mail xác thực/reset password
└── ValidationUtils.java  # Custom validators nếu cần

src/main/resources
├── application.yml  # Config DB, JPA, MyBatis, Mail, Cache, Actuator
├── templates  # Thymeleaf views
│   ├── layouts/main.html
│   ├── staff/list.html
│   ├── customer/form.html  # Với error messages từ validation
│   └── ...
├── static  # CSS/JS
│   └── css/tailwind.css  # Nếu dùng Tailwind
└── mappers  # MyBatis XML (nếu không dùng annotation)
└── AccountMapper.xml

build.gradle.kts  # Gradle Kotlin DSL cho build