package com.itwill.myblog.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itwill.myblog.domain.Image;
import com.itwill.myblog.domain.Post;
import com.itwill.myblog.service.PostService;

/**
 * Servlet implementation class PostCreateController
 */
@WebServlet(name = "postCreateController", urlPatterns = { "/post/create" })
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024 * 1, // 1MB
	    maxFileSize = 1024 * 1024 * 10,      // 10MB
	    maxRequestSize = 1024 * 1024 * 50    // 50MB
	    )
public class PostCreateController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(PostCreateController.class);
	private final PostService postService = PostService.INSTANCE;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PostCreateController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("postCreatePage::doGet");
		request.getRequestDispatcher("/WEB-INF/views/post/create.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.debug("postCreatePage::doPost");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String author = request.getParameter("author");
		Post post = Post.builder().title(title).content(content).author(author).build();
		int postId = postService.create(post);
		
		String imagePath = "C:\\java157\\workspaces\\lab-web\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\myblog\\upload\\images";
		
		String filePath = "C:\\java157\\workspaces\\lab-web\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\myblog\\upload\\files";
		log.debug("imagePath={}", imagePath);
		log.debug("filePath={}", filePath);
		
		for (Part part : request.getParts()) {
			String fileName = extractFileName(part);
			if (fileName != null && !fileName.isEmpty() && part.getContentType() != null) {
				// 고유 파일 이름 생성 (UUID 방식)
				String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
				
	            if (part.getName().equals("images")) {
	                // Save image to disk
	                String imageSavePath = imagePath + File.separator + uniqueFileName;
	                part.write(imageSavePath);

	                Image image = Image.builder().postId(postId).imageName(uniqueFileName).imagePath(imageSavePath).build();
	                
	                // Save image metadata to database
	                postService.saveImage(image);

	                log.debug("Saved image to: {}", imageSavePath);
	            } else if (part.getName().equals("files")) {
	                // Save file to disk
	                String fileSavePath = filePath + File.separator + uniqueFileName;
	                part.write(fileSavePath);

	                com.itwill.myblog.domain.File file = com.itwill.myblog.domain.File.builder()
	                		.postId(postId).fileName(uniqueFileName).filePath(fileSavePath).build();
	                
	                // Save file metadata to database
	                postService.saveFile(file);

	                log.debug("Saved file to: {}", fileSavePath);
	            }
	        }
		}
		
		String url = request.getContextPath() + "/post/details?id=" + postId;
		log.debug("redirect to {}", url);
		response.sendRedirect(url);
	}
	
	
	/**
     * Extracts the file name from the HTTP header content-disposition.
     */
    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return null;
    }

}
