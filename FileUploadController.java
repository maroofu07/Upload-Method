package com.example.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Entity.UploadedFile;
import com.example.Service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/files")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    // ✅ Upload File
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(fileStorageService.uploadFile(file));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed!");
        }
    }

    // ✅ Get All Files
    @GetMapping("/all")
    public ResponseEntity<List<UploadedFile>> getAllFiles() {
        return ResponseEntity.ok(fileStorageService.getAllFiles());
    }

    // ✅ Get File by ID
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFileById(@PathVariable Long id) {
        Optional<UploadedFile> uploadedFile = fileStorageService.getFileById(id);

        if (uploadedFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String fileName = uploadedFile.get().getFileName();
        Path filePath = Paths.get("src/main/resources/static/uploads/" + fileName);

        try {
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] fileBytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


 // Download File
    @GetMapping("/download/{id}")
    public ResponseEntity<Optional<UploadedFile>> downloadFile(@PathVariable Long fileName) throws IOException {
        Optional<UploadedFile> fileData = fileStorageService.getFileById(fileName);
		if (fileData == null) {
		    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		return ResponseEntity.ok().headers(headers).body(fileData);
    }



    
    // ✅ Delete File
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(fileStorageService.deleteFile(id));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file!");
        }
    }

    // ✅ Update File
    @PutMapping("/{id}")
    public ResponseEntity<String> updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile newFile) {
        try {
            return ResponseEntity.ok(fileStorageService.updateFile(id, newFile));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating file!");
        }
    }
}
