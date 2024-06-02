document.addEventListener('DOMContentLoaded', async () => {
    const createPostForm = document.getElementById('createPostForm');
    const accessToken = localStorage.getItem('accessToken');
    let userRole = ''; // 사용자 역할을 저장할 변수

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

    createPostForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const category = document.getElementById('category').value;
        const title = document.getElementById('title').value;
        const content = document.getElementById('content').value;

        if (userRole !== 'ADMIN' && category === 'notice') {
            alert('일반 사용자는 공지사항을 작성할 수 없습니다.');
            return;
        }

        const postData = {
            title: title,
            content: content
        };

        try {
            const response = await fetchWithAuth(`/v1/${category}/posts`, {
                method: 'POST',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(postData)
            });


            if (response.ok) {
                const postId = await response.text();
                alert('게시글 작성 성공.');
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
