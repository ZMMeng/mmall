<%@ page contentType="text/html; UTF-8" pageEncoding="utf-8"%>
<html>
<body>
<h2>Hello World!</h2>
<br/>
Spring MVC文件上传
<form name="uploadfile" action="/mmall/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="multipartFile" /><br/>
    <input type="submit" value="Spring MVC上传文件" />
</form>
<br/>
Spring MVC富文本上传
<form name="upload_rich_text_img" action="/mmall/manage/product/upload_rich_text_img.do" method="post"
      enctype="multipart/form-data">
    <input type="file" name="multipartFile" /><br/>
    <input type="submit" value="Spring MVC富文本上传" />
</form>
</body>
</html>
