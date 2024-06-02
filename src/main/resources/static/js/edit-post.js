document.addEventListener('DOMContentLoaded', () => {
    const editPostForm = document.getElementById('editPostForm');
    const categorySelect = document.getElementById('category');
    const titleInput = document.getElementById('title');
    const contentTextarea = document.getElementById('content');
    const accessToken = localStorage.getItem('accessToken');

    const urlParams = new URLSearchParams(window.location.search);
    const postId = urlParams.get('id');

    // 게시글 정보 불러오기
    if (postId) {
        fetchWithAuth(`/v1/posts/${postId}`)
            .then(response => response.json())
            .then(data => {
                titleInput.value = data.post.title;
                contentTextarea.value = data.post.content;
            })
            .catch(error => console.error('Error fetching post:', error));
    }

    // 게시글 수정하기
    editPostForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const title = titleInput.value;
        const content = contentTextarea.value;

        const postData = {
            title: title,
            content: content
        };

        try {
            const response = await fetchWithAuth(`/v1/posts/${postId}`, {
                method: 'PUT',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(postData)
            });

            if (response.ok) {
                alert('게시글 수정 성공.');
                window.location.href = `/post.html?id=${postId}`;
            } else {
                const result = await response.json();
                if (typeof result === 'object' && result !== null) {
                    const errorMessages = Object.entries(result).map(([field, message]) => `${message}`).join('\n');
                    alert(`Error:\n${errorMessages}`);
                } else {
                    alert(`Error: 알 수 없는 오류가 발생했습니다.`);
                }
            }
        } catch (error) {
            alert(`Error: ${error.message}`);
        }
    });
});