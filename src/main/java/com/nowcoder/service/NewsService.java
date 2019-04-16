package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;


@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }
    //保存图片
    public String saveImage(MultipartFile file) throws IOException {
        //getOriginalFilename()得到文件名
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        //如果没有"."就是错误格式
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        /**
         * Copies all bytes from an input stream to a file. On return, the input
         * stream will be at end of stream.
         */
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }

    public int addNews(News news) {
        newsDAO.addNews(news);
        return news.getId();
    }
    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }
    public int updateCommentCount(int id, int count){
        return newsDAO.updateCommentCount(id,count);
    }
    public int updateLikeCount(int id, int count){
        return newsDAO.updateLikeCount(id,count);
    }

}
