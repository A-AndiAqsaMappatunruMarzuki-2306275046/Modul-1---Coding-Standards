## Refleksi 1
Pada tugas ini, saya telah mengimplementasikan dua fitur baru pada aplikasi E-Shop berbasis Spring Boot, yaitu fitur Edit Product dan Delete Product. Dalam proses pengembangan fitur tersebut, saya menerapkan arsitektur Model-View-Controller (MVC) yang memisahkan tanggung jawab antara model, repository, service, controller, dan view (Thymeleaf). Pemisahan ini membantu menjaga keteraturan kode dan memastikan setiap komponen memiliki tanggung jawab yang jelas. Controller hanya menangani request dan response, service mengelola logika bisnis, sedangkan repository bertugas mengelola penyimpanan data. Dengan pendekatan ini, struktur kode yang saya buat bisa jadi lebih terorganisir, mudah dipahami, dan lebih mudah dikembangkan.

Dalam penerapan clean code principles, saya menggunakan penamaan method seperti findById(), update(), dan deleteById(), sehingga alur program lebih mudah dibaca. Saya juga menjaga agar setiap method memiliki satu tanggung jawab yang spesifik sesuai dengan prinsip Single Responsibility Principle. Struktur kode saya buat dengan konsisten dan rapi, serta memanfaatkan anotasi Spring seperti @Controller, @Service, dan @Repository untuk memperjelas peran masing-masing kelas. Selain itu, saya menggunakan redirect setelah proses create, update, dan delete untuk membantu mencegah duplicate form submission saat halaman direfresh.

Dari sisi secure coding practices, fitur edit dan delete menggunakan productId sebagai identifier unik untuk memastikan data yang dimodifikasi adalah data yang tepat. Pada controller, saya juga menambahkan pengecekan jika data tidak ditemukan agar aplikasi tidak mengalami error seperti NullPointerException. Form pada sisi frontend menggunakan atribut required dan pembatasan nilai minimum untuk mencegah input yang tidak valid. Namun, saya menyadari bahwa validasi saat ini masih dominan di sisi client dan belum menggunakan validasi berbasis anotasi di sisi server seperti @NotBlank atau @Min, sehingga ini menjadi salah satu aspek yang dapat diperbaiki.

Meskipun fitur telah berjalan dengan baik, terdapat beberapa kekurangan yang dapat ditingkatkan. Saat ini data produk masih disimpan dalam struktur List di repository sehingga bersifat in-memory dan akan hilang ketika aplikasi dihentikan. Untuk pengembangan lebih lanjut, sebaiknya aplikasi menggunakan database dan Spring Data JPA agar lebih scalable dan persisten. Selain itu, operasi delete masih dapat ditingkatkan dengan penggunaan HTTP method yang lebih tepat serta penerapan autentikasi dan otorisasi menggunakan Spring Security untuk membatasi akses terhadap fitur sensitif seperti edit dan delete.
Secara keseluruhan, melalui implementasi fitur Edit dan Delete Product ini, saya telah menerapkan konsep arsitektur berlapis, prinsip clean code, serta praktik keamanan dasar dalam pengembangan aplikasi Spring Boot. Pengalaman ini membantu saya memahami pentingnya struktur kode yang baik, pemisahan tanggung jawab, serta evaluasi berkelanjutan terhadap kualitas dan keamanan kode yang saya tulis.


## Refleksi 2

**1. Refleksi mengenai Unit Testing dan Code Coverage**

Setelah menulis *unit test* untuk model `Product`, kelas `ProductRepository`, dan mengeksplorasi *functional test*, saya merasa lebih yakin dengan keandalan kode yang saya buat. Menulis pengujian memaksa saya untuk berpikir kritis tentang berbagai kemungkinan (seperti *edge cases*) dan potensi kegagalan sistem, bahkan sebelum kode tersebut benar-benar dijalankan.

Mengenai berapa banyak *unit test* yang harus dibuat dalam satu kelas, sebenarnya tidak ada angka pasti. Jumlah pengujian harus berbanding lurus dengan tingkat kompleksitas *method* dan banyaknya jalur logika eksekusi yang ada di dalam kelas tersebut. Idealnya, kita harus memastikan pengujian mencakup **skenario positif** (ketika input valid dan operasi berhasil) dan **skenario negatif** (ketika input tidak valid, kosong/null, atau saat operasi seharusnya gagal).

Untuk mengukur sejauh mana pengujian kita sudah menyeluruh, kita bisa menggunakan metrik **Code Coverage** (seperti *line coverage* dan *branch coverage*). Namun, mencapai 100% *code coverage* **tidak menjamin** bahwa kode kita 100% bebas dari *bug* atau *error*. Cakupan 100% hanya berarti setiap baris kode telah dieksekusi setidaknya satu kali selama proses *testing*, tetapi tidak berarti bahwa tes tersebut telah memverifikasi logika bisnis dengan benar. Kesalahan logika, masalah konkurensi, atau salah pemahaman terhadap spesifikasi fungsional masih sangat mungkin terjadi dan terlewat dari laporan 100% *coverage* jika *assertion* (pengecekan kebenaran nilai) di dalam tesnya lemah atau tidak tepat sasaran.

**2. Refleksi mengenai Kebersihan dan Kualitas Functional Test**

Jika saya membuat kelas *functional test* baru untuk memverifikasi jumlah item di daftar produk dengan cara menyalin (*copy-paste*) prosedur *setup* dan *instance variables* (seperti `@LocalServerPort`, `testBaseUrl`, dan metode `@BeforeEach`) dari `CreateProductFunctionalTest.java`, hal itu akan sangat berdampak buruk pada kualitas dan kebersihan kode (*clean code*).

Masalah utama dari sisi *clean code* di sini adalah pelanggaran prinsip **DRY (Don't Repeat Yourself)**.

**Alasan mengapa kualitas kode menurun:**
* **Beban Pemeliharaan (*Maintenance Overhead*):** Jika di masa depan ada perubahan pada konfigurasi server, *base URL*, atau pengaturan Selenium, saya harus mencari dan mengubah kode yang sama di setiap file pengujian secara manual. Ini sangat rentan terhadap *human error* dan membuat pengujian menjadi rapuh.
* **Menurunkan Keterbacaan (*Readability*):** Kode *setup* yang diulang-ulang akan membuat *file* pengujian menjadi panjang dan membengkak. Ini menyulitkan *developer* (atau saya sendiri di kemudian hari) untuk fokus membaca inti dari logika pengujiannya (fase *Exercise* dan *Verify*).

**Saran Perbaikan:**
Untuk membuat kode menjadi lebih bersih (*cleaner*), kita bisa memanfaatkan konsep **Inheritance** (Pewarisan) di Java. Kita dapat mengekstrak dan memindahkan semua konfigurasi *setup* serta *instance variable* yang identik tersebut ke dalam satu *Base Class* (kelas induk), misalnya `BaseFunctionalTest.java`.

```java
// Contoh penerapan Base Class
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
public abstract class BaseFunctionalTest {
    @LocalServerPort
    protected int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    protected String testBaseUrl;

    protected String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }
}
```

## 4.2 Reflection

**1. List the code quality issue(s) that you fixed during the exercise and explain your strategy on fixing them.**

Selama mengerjakan latihan ini, saya memperbaiki beberapa isu kualitas kode yang dideteksi oleh SonarCloud:
* **Duplicated String Literal (`ProductController.java`):** SonarCloud mendeteksi penggunaan *string* `"redirect:/product/list"` yang diketik ulang sebanyak tiga kali. Strategi perbaikannya adalah menerapkan prinsip *Clean Code* dengan mengekstrak *string* tersebut ke dalam satu variabel konstanta (`private static final String`). Hal ini mempermudah pemeliharaan (*maintainability*) jika sewaktu-waktu URL perlu diubah, serta mencegah risiko salah ketik (*typo*).
* **Empty Methods (`EshopApplicationTests.java` dan `ProductRepositoryTest.java`):** SonarCloud menandai metode kosong seperti `contextLoads()` dan `setUp()` sebagai *Code Smell* karena niat pembuat kode menjadi tidak jelas. Strategi untuk memperbaikinya adalah dengan menambahkan komentar bersarang (*nested comment*) di dalam blok metode tersebut yang menjelaskan secara eksplisit bahwa metode itu memang sengaja dikosongkan (misalnya untuk sekadar menguji proses *loading Spring Context*).

**2. Look at your CI/CD workflows (GitHub)/pipelines (GitLab). Do you think the current implementation has met the definition of Continuous Integration and Continuous Deployment? Explain the reasons.**

Ya, implementasi *workflow* pada repositori ini sudah memenuhi definisi *Continuous Integration* (CI) dan *Continuous Deployment* (CD).

CI telah tercapai karena setiap kali ada kode baru yang di-*push* atau di-*merge* ke dalam *branch*, GitHub Actions akan secara otomatis menjalankan seluruh rangkaian *unit test* (beserta JaCoCo) dan memindai kualitas kode menggunakan SonarCloud untuk mencegah *bug* atau penurunan kualitas masuk ke repositori utama.

Sementara itu, CD juga telah terwujud melalui integrasi antara *branch* `main` dan layanan PaaS, di mana setiap perubahan kode di *branch* utama akan secara otomatis memicu proses *build container* Docker dan meluncurkan pembaruan aplikasi ke server tanpa memerlukan intervensi manual sama sekali.

## SOLID Principles Implementation

**1. Have you implemented SRP?**
Yes, I have applied the Single Responsibility Principle (SRP). Previously, the `CarController` was placed inside the `ProductController.java` file. I separated them into two different files so that `ProductController` only handles product-related logic, and `CarController` only handles car-related logic. Each class now encapsulates only one aspect of the software's functionality and has only one reason to change.

**2. Have you implemented OCP?**
Yes, the Open-Closed Principle (OCP) is applied. By injecting the `CarService` interface into the controller rather than its concrete implementation, the system is open for extension but closed for modification. If we need to implement a new car service logic (e.g., fetching data from a real database), we can simply create a new class implementing `CarService` without modifying the `CarController` source code.

**3. Have you implemented LSP?**
Yes, I applied the Liskov Substitution Principle (LSP). In the previous code, `CarController` used the `extends ProductController` keyword. This violated LSP because a `CarController` cannot perfectly substitute a `ProductController` (they handle different domains entirely). I removed the inheritance, ensuring that class hierarchies are semantically correct.

**4. Have you implemented ISP?**
Yes, the Interface Segregation Principle (ISP) is applied. The `CarService` interface is focused and specific, containing only the methods (`create`, `findAll`, `findById`, `update`, `deleteCarById`) that the client (`CarController`) actually needs to know and use. It is not bloated with irrelevant methods.

**5. Have you implemented DIP?**
Yes, I applied the Dependency Inversion Principle (DIP). Previously, the `CarController` depended on a concrete, low-level module: `@Autowired private CarServiceImpl carservice`. I changed this to depend on the high-level abstraction: `@Autowired private CarService carservice`. Now, the controller relies on the abstraction rather than the implementation detail.


## Reflection

**1) Explain what principles you apply to your project!**

In this project, I applied several SOLID principles to refactor the initial, tightly-coupled code:
* **Single Responsibility Principle (SRP):** I extracted `CarController` from the `ProductController.java` file and placed it in its own file. Now, `ProductController` only handles product-related HTTP requests, and `CarController` only handles car-related requests.
* **Liskov Substitution Principle (LSP):** I removed the `extends ProductController` inheritance from the `CarController` class. A `CarController` is not a logical substitute for a `ProductController`, so inheriting from it was a structural mistake.
* **Dependency Inversion Principle (DIP):** In the `CarController`, I changed the injected dependency from the concrete implementation (`CarServiceImpl`) to its abstraction (`CarService` interface).
* **Open-Closed Principle (OCP):** By relying on the `CarService` interface, the controller is now open for extension (e.g., adding a new database service implementation) but closed for modification.

**2) Explain the advantages of applying SOLID principles to your project with examples.**

Applying SOLID principles makes the codebase much easier to maintain, scale, and test:
* **Improved Maintainability & Readability (SRP Example):** By separating `CarController` and `ProductController`, I prevent the creation of a "God Class." If a bug occurs in the car editing feature, I know exactly where to look (`CarController.java`) without having to sift through hundreds of lines of irrelevant product logic.
* **High Flexibility & Loose Coupling (DIP & OCP Example):** Because `CarController` depends on the `CarService` interface, I can easily swap out the underlying data storage in the future. If I want to upgrade from using an in-memory list to a real PostgreSQL database, I simply create a new class (e.g., `CarServiceDbImpl`) that implements `CarService`. I won't need to change a single line of code inside the `CarController`.

**3) Explain the disadvantages of not applying SOLID principles to your project with examples.**

Failing to apply SOLID principles leads to a rigid, fragile, and error-prone system:
* **Unexpected Bugs & Side Effects (LSP Violation Example):** When `CarController` was extending `ProductController`, it silently inherited all product-related endpoints. This means a user navigating to a car-related path might accidentally trigger a product-related action, causing routing conflicts and potential security flaws.
* **Testing Difficulties & Tight Coupling (DIP Violation Example):** When `CarController` directly depended on the concrete `CarServiceImpl`, the two classes were tightly coupled. When I tried to run unit tests for the `ProductController`, the application context crashed because it was trying to load the tightly-coupled `CarServiceImpl` dependency that wasn't mocked. This makes isolated unit testing incredibly frustrating and difficult.


## Reflection (Module 4)

**1. Reflect based on Percival (2017) proposed self-reflective questions, whether this TDD flow is useful enough for you or not. If not, explain things that you need to do next time you make more tests.**

Based on Percival's self-reflective framework, this Test-Driven Development (TDD) flow has proven to be highly useful for my development process. By strictly following the Red-Green-Refactor cycle, I experienced several benefits:
* **Confidence in Code:** Writing the failing tests first forced me to deeply understand the requirements and edge cases (e.g., handling invalid order statuses or all-lowercase author names) before writing any actual logic. When the tests finally turned green, I had high confidence that my code behaved exactly as intended.
* **Better Design:** TDD guided me to write modular and testable code. For instance, testing the `OrderService` required me to use Mockito to isolate the service layer from the repository layer, which naturally enforced the Dependency Inversion Principle.
* **Room for Improvement:** While useful, one thing I need to improve next time is anticipating more complex "unhappy paths." Currently, the tests cover the explicit edge cases mentioned in the requirements, but in the future, I should proactively think of hidden boundary conditions (e.g., extremely long strings or concurrent modifications) to make the test suite even more robust.

**2. You have created unit tests in Tutorial. Now reflect whether your tests have successfully followed F.I.R.S.T. principle or not. If not, explain things that you need to do the next time you create more tests.**

Yes, the unit tests I created have largely successfully followed the **F.I.R.S.T.** principles:
* **Fast:** The tests run extremely quickly (in milliseconds) because they don't rely on a real database or external network. We used in-memory lists and Mockito to ensure fast execution.
* **Independent:** Each test is isolated. I used the `@BeforeEach` annotation to set up a fresh `List<Product>` and `List<Order>` before every single test, ensuring that data mutation in one test does not affect the outcome of another.
* **Repeatable:** The tests can be run in any environment (my local machine, CI/CD pipeline, etc.) and will yield the exact same results every time because there are no external dependencies.
* **Self-Validating:** All tests use assertions (e.g., `assertEquals`, `assertThrows`, `assertNull`) to automatically output a pass or fail boolean result. I do not have to manually check logs or print statements to verify the output.
* **Timely:** The tests were written *before* the production code, perfectly adhering to the TDD methodology.

**What to do next time:** To further improve, I need to ensure that my mock setups in the future remain strictly focused on behavior rather than tightly coupling to implementation details. Sometimes, over-mocking can make tests brittle, so finding the right balance of what to mock will be my focus for the next tests.