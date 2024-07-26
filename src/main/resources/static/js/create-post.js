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


            const result = await response.json();
            if (response.ok) {
                alert('게시글 작성 성공.');
                window.location.href = `/post.html?id=${result.response}`;
            } else {
                let errorMessages = '';
                for (const [field, message] of Object.entries(result)) {
                    errorMessages += `${field}: ${message}\n`;
                }
                alert(`Error: ${errorMessages}`);
            }
        } catch (error) {
            alert(`Error: ${error.message}`);
        }
    });
});
