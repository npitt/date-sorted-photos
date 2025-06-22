# date-sorted-photos

本專案是一個 Spring Boot 應用程式，可將本機照片依照檔案建立日期自動分類到不同資料夾。

## 功能說明

- 讀取來源資料夾內所有檔案，依據檔案建立日期（creationTime）分類。
- 依照年月（yyyy-MM）自動建立目標資料夾，並將照片複製到對應資料夾下。
- 支援 Windows 路徑與中文目錄。

## 使用方式

### 1. 設定來源與目標資料夾

請編輯 `src/main/resources/application.yml`，設定來源與目標資料夾路徑，例如：

```yaml
photo-sorter:
  source-folder: "C:\\Users\\你的帳號\\Pictures\\Source"
  destination-folder: "C:\\Users\\你的帳號\\Pictures\\Sorted"
```

### 2. 執行專案

在專案根目錄下執行：

```sh
mvn spring-boot:run
```

或直接執行 main 方法。

### 3. 分類結果

- 程式會自動在目標資料夾下依年月建立子資料夾（如 `2024-01`），並將照片複製進去。
- 若目標資料夾不存在會自動建立。

## 測試

執行：

```sh
mvn test
```

測試會自動建立臨時資料夾與檔案，驗證分類邏輯。

## 依賴

- Java 17 以上
- Spring Boot 3.5.x
- Lombok

## 注意事項

- 來源資料夾需存在且有照片檔案。
- 目標資料夾若有同名檔案會被覆蓋。
- 僅依檔案建立日期分類，無法處理 EXIF 拍攝日期。
