package com.example.Service;

import com.example.Entity.UploadedFile;
import com.example.Repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @Autowired
    private FileRepository fileRepository;

    // Upload file
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "File is empty!";
        }

        // Ensure directory exists
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save file to system
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save file info in DB
        UploadedFile uploadedFile = new UploadedFile(fileName);
        fileRepository.save(uploadedFile);

        return "File uploaded successfully: " + fileName;
    }

    // Get all files
    public List<UploadedFile> getAllFiles() {
        return fileRepository.findAll();
    }

    // Get file by ID
    public Optional<UploadedFile> getFileById(Long id) {
        return fileRepository.findById(id);
    }

    // Delete file by ID
    public String deleteFile(Long id) throws IOException {
        Optional<UploadedFile> uploadedFile = fileRepository.findById(id);
        if (uploadedFile.isEmpty()) {
            return "File not found in database!";
        }

        String fileName = uploadedFile.get().getFileName();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.deleteIfExists(filePath);
        fileRepository.deleteById(id);

        return "File deleted successfully: " + fileName;
    }

    // Update file
    public String updateFile(Long id, MultipartFile newFile) throws IOException {
        Optional<UploadedFile> uploadedFile = fileRepository.findById(id);
        if (uploadedFile.isEmpty()) {
            return "File not found in database!";
        }

        String oldFileName = uploadedFile.get().getFileName();
        Path oldFilePath = Paths.get(UPLOAD_DIR + oldFileName);
        Files.deleteIfExists(oldFilePath);

        // Save new file
        String newFileName = newFile.getOriginalFilename();
        Path newFilePath = Paths.get(UPLOAD_DIR + newFileName);
        Files.copy(newFile.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

        // Update DB
        UploadedFile fileRecord = uploadedFile.get();
        fileRecord.setFileName(newFileName);
        fileRepository.save(fileRecord);

        return "File updated successfully: " + newFileName;
    }
}
