package A1B1O3.bodyrecord.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class UploadFile {
    @Value("${image.image-dir}")
    private String uploadPath;

    public String makeDir(String dirName){
        String exerciseImages = "/" + dirName;
        if(!new File(uploadPath + exerciseImages, exerciseImages).exists()) {
            new File(uploadPath, exerciseImages).mkdir();
        }
        return exerciseImages;

    }

    public String fileUpload(MultipartFile file) throws IOException {
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();
        String dir = makeDir("exerciseImages");
        File saveFile = new File(uploadPath + dir, fileName);
        saveFile.createNewFile();
        file.transferTo(saveFile);
        return (dir + "/" + fileName);
    }

    public String profileUpload(MultipartFile file) throws IOException {
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();
        String dir = makeDir("profileImages");
        File saveFile = new File(uploadPath+dir, fileName);
        saveFile.createNewFile();
        file.transferTo(saveFile);
        return (dir + "/" + fileName);
    }

    public String saveChallengeImage(MultipartFile challengeImageFile) throws IOException {
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + challengeImageFile.getOriginalFilename();
        String dir =makeDir("challengeImages");
        File saveFile = new File(uploadPath+dir, fileName);
        challengeImageFile.transferTo(saveFile);
        return (dir + "/" + fileName);
    }

}
