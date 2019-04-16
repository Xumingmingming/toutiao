package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 图片上传功能
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    QiniuService qiniuService;
    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;

    @RequestMapping(value = "/news/{newsId}",method = RequestMethod.GET)
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        try {
            News news = newsService.getById(newsId);
            if (news != null) {
                //点赞
                int localUserId=hostHolder.getUser().getId();
                if (localUserId!=0){
                    int likeCount=likeService.getLikeStatus(localUserId,EntityType.ENTITY_NEWS,news.getId());
                    model.addAttribute("like",likeCount);
                }else {
                    model.addAttribute("like",0);
                }
                //评论
                List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<ViewObject>();
                for (Comment comment : comments) {
                    ViewObject commentVO = new ViewObject();
                    commentVO.set("comment", comment);
                    commentVO.set("user", userService.getUser(comment.getUserId()));
                    commentVOs.add(commentVO);
                }
                model.addAttribute("comments", commentVOs);
            }
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        } catch (Exception e) {
            logger.error("获取资讯明细错误" + e.getMessage());
        }

        return "detail";
    }

    //查看图片
    @RequestMapping(value = "/image",method = RequestMethod.GET)
    public void getImage(@RequestParam("name") String imageName,
                           HttpServletResponse httpServletResponse){
        try {
            //设置Body中的内容格式
            httpServletResponse.setContentType("image/jpg");
            /**
             * StreamUtils.copy(InputStream in, OutputStream out)
             *  将图片的二进制从输入流复制到输出流
             */
            StreamUtils.copy(new FileInputStream(new
                    File(ToutiaoUtil.IMAGE_DIR + imageName)), httpServletResponse.getOutputStream());
        } catch (Exception e) {
            logger.error("读取图片错误" + imageName + e.getMessage());
        }
    }

    /**
     *
     * @param file 向服务器上传的图片的二进制流
     * @return
     */
    //图片传输的时候就是二进制流，不需要模板渲染，所以用@ResponseBody
    @RequestMapping(value = "/uploadImage/",method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){
      try{
          //上传图片到本地
          //String fileUrl=newsService.saveImage(file);
          //上传图片到七牛云
          String fileUrl = qiniuService.saveImage(file);
          if (fileUrl==null){
              return ToutiaoUtil.getJSONString(1, "上传图片失败");
          }
          return ToutiaoUtil.getJSONString(0, fileUrl);
      }catch (Exception e){
          logger.error("上传图片失败"+e.getMessage());
          return ToutiaoUtil.getJSONString(1,"上传失败");
      }
    }
    //增加咨询
    @RequestMapping(value = "/user/addNews/",method = RequestMethod.POST)
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link){
      try {
          News news = new News();
          news.setCreatedDate(new Date());
          news.setTitle(title);
          news.setImage(image);
          news.setLink(link);
          if (hostHolder.getUser() != null) {
              news.setUserId(hostHolder.getUser().getId());
          }
          //设置了一个匿名用户
          else {
              news.setUserId(3);
          }
          newsService.addNews(news);
          return ToutiaoUtil.getJSONString(0);
      }catch (Exception e){
          logger.error("添加失败"+e.getMessage());
          return ToutiaoUtil.getJSONString(1,"添加失败！");
      }
    }
    //增加评论
    @RequestMapping(value = "/addComment",method = RequestMethod.POST)
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){

        try {
            Comment comment=new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);
            //查看type=news的这条new（由newid标识）有多少条评论
            int count=commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            //更新news里的评论数量
            newsService.updateCommentCount(comment.getEntityId(),count);

        }catch (Exception e){
            logger.error("添加评论失败"+e.getMessage());
        }
        return "redirect:/news/"+String.valueOf(newsId);
    }


}
