<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/jsp/header.jsp" />

<section id="admin-product-form-section" style="padding: 20px; max-width: 700px; margin: auto;">
    <h2>
        <c:choose>
            <c:when test="${isEditMode}">编辑商品 (Edit Product)</c:when>
            <c:otherwise>添加新商品 (Add New Product)</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty errorMessage}">
        <p style="color:red; border: 1px solid red; padding: 10px;"><c:out value="${errorMessage}"/></p>
    </c:if>
    <c:if test="${not empty param.saveError}">
        <p style="color:red; border: 1px solid red; padding: 10px;"><c:out value="${param.saveError}"/></p>
    </c:if>


    <form action="${pageContext.request.contextPath}/admin/saveProduct" method="POST" enctype="multipart/form-data">
        <c:if test="${isEditMode}">
            <input type="hidden" name="productId" value="${productToEdit.id}">
        </c:if>

        <div class="form-group">
            <label for="name">商品名称 (Product Name):</label>
            <input type="text" id="name" name="name" value="<c:out value='${productToEdit.name}'/>" required>
        </div>

        <div class="form-group">
            <label for="description">商品描述 (Description):</label>
            <textarea id="description" name="description" rows="4"><c:out value='${productToEdit.description}'/></textarea>
        </div>

        <div class="form-group">
            <label for="price">价格 (Price):</label>
            <input type="number" id="price" name="price" value="${productToEdit.price}" step="0.01" min="0" required>
        </div>

        <div class="form-group">
            <label for="stock">库存 (Stock):</label>
            <input type="number" id="stock" name="stock" value="${productToEdit.stock}" min="0" required>
        </div>

        <div class="form-group">
            <label for="categoryId">分类 (Category):</label>
            <select id="categoryId" name="categoryId" required>
                <option value="">-- 选择分类 --</option>
                <c:forEach var="cat" items="${categoryList}">
                    <option value="${cat.id}" ${cat.id == productToEdit.categoryId ? 'selected' : ''}>
                        <c:out value="${cat.name}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="productImage">商品图片 (Product Image):</label>
            <c:if test="${isEditMode && not empty productToEdit.imageUrl}">
                <div style="margin-bottom: 10px;">
                    <p>当前图片 (Current Image):</p>
                    <img src="${pageContext.request.contextPath}/${productToEdit.imageUrl}" alt="Current Product Image" style="max-width: 100px; max-height: 100px; border:1px solid #ddd;">
                    <input type="hidden" name="existingImageUrl" value="${productToEdit.imageUrl}">
                </div>
                <p>上传新图片以替换 (Upload new image to replace):</p>
            </c:if>
            <input type="file" id="productImage" name="productImage" accept="image/*">
        </div>

        <div class="form-group">
            <label for="status">商品状态 (Status):</label>
            <select id="status" name="status" required>
                <option value="上架" ${productToEdit.status == '上架' ? 'selected' : ''}>上架 (On Sale)</option>
                <option value="下架" ${productToEdit.status == '下架' ? 'selected' : ''}>下架 (Off Sale)</option>
                <option value="新品" ${productToEdit.status == '新品' ? 'selected' : ''}>新品 (New)</option>
                <option value="热销推荐" ${productToEdit.status == '热销推荐' ? 'selected' : ''}>热销推荐 (Hot Recommendation)</option>
            </select>
        </div>

        <div class="form-actions" style="margin-top: 20px;">
            <button type="submit" class="admin-button save">保存商品 (Save Product)</button>
            <a href="${pageContext.request.contextPath}/admin/viewProducts" class="admin-button cancel">取消 (Cancel)</a>
        </div>
    </form>
</section>

<style>
    .form-group {
        margin-bottom: 15px;
    }
    .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }
    .form-group input[type="text"],
    .form-group input[type="number"],
    .form-group textarea,
    .form-group select,
    .form-group input[type="file"] {
        width: 100%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .form-group textarea {
        resize: vertical;
    }
    .admin-button {
        padding: 10px 15px;
        text-decoration: none;
        border-radius: 4px;
        border: none;
        cursor: pointer;
        color: white !important;
        display: inline-block;
        margin-right: 10px;
    }
    .admin-button.save {
        background-color: #5cb85c; /* Green */
    }
    .admin-button.save:hover {
        background-color: #4cae4c;
    }
    .admin-button.cancel {
        background-color: #777; /* Gray */
    }
    .admin-button.cancel:hover {
        background-color: #5a5a5a;
    }
</style>

<jsp:include page="/jsp/footer.jsp" />
