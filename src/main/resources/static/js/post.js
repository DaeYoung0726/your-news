document.addEventListener('DOMContentLoaded', async () => {
    const editPostButton = document.getElementById('editPostButton');
    const deletePostButton = document.getElementById('deletePostButton');
    const likePostButton = document.getElementById('likePostButton');
    const categoryInput = document.getElementById('category');
    const titleInput = document.getElementById('title');
    const authorInput = document.getElementById('author');
    const contentTextarea = document.getElementById('content');
    const postLikes = document.getElementById('postLikes');
    const mainPageButton = document.getElementById('mainPageButton');

    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('id');
    const accessToken = localStorage.getItem('accessToken');

    let userRole = ''; // 사용자 역할을 저장할 변수
    var currentLikeCount = 0;
    let categoryName = '';

    async function getUserRole() {
        try {
            const response = await fetchWithAuth('/v1/auth/user-role', {
                method: 'GET',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const data = await response.json();
                userRole = data.role;
            } else {
                throw new Error('사용자 역할 정보를 가져오지 못했습니다.');
            }
        } catch (error) {
            alert(`Error: ${error.message}`);
            window.location.href = '/';
        }
    }

    await getUserRole();

    editPostButton.addEventListener('click', () => {
        window.location.href = `/edit-post.html?id=${postId}`;
    });

    mainPageButton.addEventListener('click', () => {
        window.location.href = '/main.html';
    });

    deletePostButton.addEventListener('click', async () => {
        const confirmed = confirm('정말 이 게시글을 삭제하시겠습니까?');
        if (confirmed) {
            try {
                const deleteUrl = userRole === 'ADMIN' ? `/v1/admin/posts/${categoryName}/${postId}` : `/v1/posts/${postId}`;
                const response = await fetchWithAuth(deleteUrl, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': accessToken,
                        'Content-Type': 'application/json'
                    }
                });

                const result = await response.json();
                if (response.ok) {
                    alert(result.response);
                    window.location.href = '/main.html';
                } else {
                    alert(`Error: ${result.message}`);
                }
            } catch (error) {
                alert(`Error: ${error.message}`);
            }
        }
    });

    // 게시글 정보 불러오기
    if (postId) {
        fetchWithAuth(`/v1/posts/${postId}`, {
            method: 'GET',
            headers: {
                'Authorization': accessToken,
                'Content-Type': 'application/json'
            }
        })
            .then(response => response.json())
            .then(data => {

                const post = data.post;
                const isAuthor = data.isAuthor;

                titleInput.value = post.title;
                authorInput.value = post.writerNickname;
                contentTextarea.value = post.content;
                postLikes.textContent = post.likeCount;
                currentLikeCount = post.likeCount;
                categoryName = post.category;

                if (post.category === "notice") {
                    categoryInput.value = "공지사항";
                } else if (post.category === "news-request") {
                    categoryInput.value = "소식 추가 요청";
                } else {
                    categoryInput.value = post.category;
                }

                if (isAuthor || userRole === 'ADMIN') {
                    editPostButton.style.display = 'block';
                    deletePostButton.style.display = 'block';
                } else {
                    editPostButton.style.display = 'none';
                    deletePostButton.style.display = 'none';
                }

            })
            .catch(error => console.error('Error fetching post:', error));
    }

    // 좋아요 버튼 클릭
    likePostButton.addEventListener('click', async () => {
        try {
            const response = await fetchWithAuth(`/v1/posts/${postId}/like`, {
                method: 'POST',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();
            if (response.ok) {
                alert(result.response);
                postLikes.textContent = currentLikeCount + 1;
            } else {
                alert(`Error: ${result.message}`);
            }
        } catch (error) {
            alert(`Error: ${error.message}`);
        }
    });
});
