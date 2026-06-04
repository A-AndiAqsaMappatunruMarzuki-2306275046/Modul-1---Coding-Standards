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