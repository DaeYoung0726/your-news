document.addEventListener('DOMContentLoaded', () => {
    const createPostButton = document.getElementById('createPostButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const logoutButton = document.getElementById('logoutButton');
    const editInfoButton = document.getElementById('editInfoButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const editSubNewsButton = document.getElementById('editSubNewsButton');
    const deleteAccountButton = document.getElementById('deleteAccountButton');

    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const nicknameInput = document.getElementById('nickname');
    const subNewsInput = document.getElementById('subNews');
    const accessToken = localStorage.getItem('accessToken');

    // 페이지 로드 시 사용자 정보 불러오기
    fetchWithAuth('/v1/users', {
        method: 'GET',
        headers: {
            'Authorization': accessToken,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            usernameInput.value = data.username;
            emailInput.value = data.email;
            nicknameInput.value = data.nickname;
            subNewsInput.value = data.subNews.map(news => news.news).join(', ');
        })
        .catch(error => console.error('Error fetching user info:', error));

    createPostButton.addEventListener('click', () => {
        window.location.href = '/create-post.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });

    logoutButton.addEventListener('click', async () => {
        try {
            const response = await fetch('/logout', {
                method: 'POST',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                window.location.href = '/';
            } else {
                alert('로그아웃 실패. 다시 시도해주세요.');
            }
        } catch (error) {
            alert(`Error: ${error.message}`);
        }
    });

    mainPageButton.addEventListener('click', () => {
        window.location.href = '/main.html';
    });

    editInfoButton.addEventListener('click', () => {
        window.location.href = '/edit-user-info.html';
    });

    editSubNewsButton.addEventListener('click', () => {
        window.location.href = '/edit-sub-news.html';
    });

    deleteAccountButton.addEventListener('click', async () => {
        const confirmed = confirm('정말 탈퇴하시겠습니까?');
        if (confirmed) {
            try {
                const response = await fetchWithAuth('/v1/users', {
                    method: 'DELETE',
                    headers: {
                        'Authorization': accessToken,
                        'Content-Type': 'application/json'
                    }
                });

                const result = await response.json();
                if (response.ok) {
                    alert(result.response);
                    window.location.href = '/';
                } else {
                    alert(`Error: ${result.message}`);
                }
            } catch (error) {
                alert(`Error: ${error.message}`);
            }
        }
    });
});
