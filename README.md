# Not Defteri

Kişisel kullanım için blok tabanlı bir not/sayfa yönetim uygulaması: hiyerarşik sayfalar,
zengin metin blokları (başlık, paragraf, madde işareti, yapılacaklar) ve yerel/bulut AI
destekli özellikler (özetleme, genişletme, düzeltme ve sayfa içeriğine dayalı soru-cevap).

Bu proje [Claude](https://claude.com/claude-code) ile (Claude Code CLI üzerinden) uçtan uca
geliştirilmiştir: backend, frontend, Docker yapılandırması ve dokümantasyon dahil.

## Teknoloji Yığını

- **Backend:** Java 21, Spring Boot, Spring Data JPA, Spring WebFlux (`WebClient`)
- **Veritabanı:** PostgreSQL + [pgvector](https://github.com/pgvector/pgvector) (embedding vektörleri için)
- **Frontend:** React + Tailwind CSS
- **Yerel AI:** [Ollama](https://ollama.com) (`nomic-embed-text` embedding modeli) — ileride Raspberry Pi üzerinde
- **Bulut AI:** Google Gemini API — metin özetleme/genişletme/düzeltme ve RAG soru-cevap
- **Dağıtım:** Docker Compose (postgres+pgvector, backend, frontend)

## Özellikler

- Çok kullanıcılı: kayıt/giriş (JWT), her kullanıcı sadece kendi oluşturduğu sayfaları
  görür/arar/AI ile sorgular — arama ve RAG dahil her sorgu giriş yapan kullanıcıya göre
  filtrelenir, başka bir kullanıcının notu hiçbir şekilde sızmaz
- Hiyerarşik sayfa ağacı (sınırsız derinlikte alt sayfa), arama, sayfa oluşturma/silme
- Blok tabanlı editör: başlık (H1-H3), paragraf, madde işareti, yapılacaklar (checkbox)
- Sayfa kaydedildiğinde bloklar arka planda (async) embed edilip pgvector'a yazılır
- RAG tabanlı soru-cevap: soru embed edilir, pgvector cosine similarity ile en alakalı
  parçalar bulunur, bulunan bağlam + soru Gemini'ye gönderilir
- Seçili metin üzerinde AI aksiyonları: Özetle / Genişlet / Düzelt
- Sağ panelde AI sohbet arayüzü, mesaj geçmişi ile
- Açık/koyu tema

## Proje Yapısı

```
backend/    Spring Boot API (entity / repository / service / controller)
frontend/   React + Tailwind arayüzü
docker-compose.yml
.env.example
```

## Yerel Geliştirme

### Gereksinimler

- Java 21, Node.js 20+
- Docker (Postgres+pgvector için) veya yerel bir Postgres + `pgvector` eklentisi
- [Ollama](https://ollama.com) kurulu ve `nomic-embed-text` modeli çekilmiş (`ollama pull nomic-embed-text`)
- Bir Google Gemini API anahtarı

### Adımlar

1. `.env.example` dosyasını `.env` olarak kopyalayın ve gerçek değerleri girin (bu dosya
   asla commit edilmez). `JWT_SECRET` için en az 32 karakterlik rastgele bir değer üretin:
   ```
   openssl rand -base64 32
   ```
2. Sadece veritabanını Docker ile ayağa kaldırın:
   ```
   docker compose up -d postgres
   ```
3. Backend'i çalıştırın (`.env` içindeki değerleri ortam değişkeni olarak export edin
   veya IDE'nizin çalıştırma yapılandırmasına ekleyin):
   ```
   cd backend
   ./mvnw spring-boot:run
   ```
4. Frontend'i çalıştırın:
   ```
   cd frontend
   npm install
   npm run dev
   ```
5. Tarayıcıda açılan ekranda "Kayıt Ol" sekmesinden bir hesap oluşturun — sayfalar
   kullanıcıya özeldir, önce bir hesap gerekir.

### Her şeyi Docker Compose ile çalıştırmak

```
docker compose up -d --build
```

## Raspberry Pi Kurulum / TODO

Aşağıdaki adımlar **şu an uygulanmamıştır**, sadece ileride Pi üzerinde (ayrıca Claude Code
ile) yapılacak kurulumun dokümantasyonudur:

- [ ] Raspberry Pi üzerine [Ollama](https://ollama.com) kurulumu
- [ ] `ollama pull nomic-embed-text` ile embedding modelinin Pi'ye çekilmesi
- [ ] pgvector Docker image'ının (`pgvector/pgvector`) Pi'de `docker compose` ile ayağa kaldırılması
- [ ] Pi üzerinde `.env` dosyasının oluşturulması — gerçek `GEMINI_API_KEY` ve DB
      kimlik bilgileriyle; bu dosya **asla** commit edilmeyecek
- [ ] `docker compose up -d` ile postgres + backend + frontend servislerinin başlatılması
- [ ] Backend'in `OLLAMA_BASE_URL` değerinin Pi'nin yerel ağ adresine göre ayarlanması
      (örn. `http://raspberrypi.local:11434` ya da Pi'nin sabit LAN IP'si)
- [ ] Frontend'in `VITE_API_BASE_URL` değerinin (build-time) Pi'nin LAN adresine göre
      ayarlanması, aksi halde tarayıcı `localhost`'a istek atmaya çalışır
- [ ] Gerekirse ters proxy / dış erişim (Tailscale, Caddy vb.) yapılandırılması

## Güvenlik

- Hiçbir API anahtarı, şifre veya secret değer bu repoda commit edilmez.
- `application.yml` sadece `${GEMINI_API_KEY}` gibi ortam değişkeni referansları içerir,
  gerçek değer içermez.
- `.gitignore`, `.env`, `application-local.yml`, `node_modules/`, `target/` ve `*.log`
  dosyalarını hariç tutar.
- Gerçek değerler yalnızca `.env` (commit edilmeyen) veya deployment ortamının kendi
  secret yönetiminden sağlanır.
