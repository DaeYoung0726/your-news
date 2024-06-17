document.addEventListener('DOMContentLoaded', async () => {
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const userManagementButton = document.getElementById('userManagementButton');
    const newsManagementButton = document.getElementById('newsManagementButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');

    const deleteAccountButton = document.getElementById('deleteAccountButton');

    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const nicknameInput = document.getElementById('nickname');
    const subNewsInput = document.getElementById('subNews');
    const accessToken = localStorage.getItem('accessToken');

    const urlParams = new URLSearchParams(window.location.search);
    const memberId = urlParams.get('id');
    const memberNickname = urlParams.get('nickname');

    // 사용자 정보 불러오기
    let fetchUrl;
    if (memberId) {
        fetchUrl = `/v1/admin/users/${memberId}`;
    } else if (memberNickname) {
        fetchUrl = `/v1/admin/users/by-nickname?nickname=${memberNickname}`;
    } else {
        console.error('Neither id nor nickname provided in URL.');
        return;
    }

    fetchWithAuth(fetchUrl, {
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


    logoutButton.addEventListener('click', async () => {
        try {
            const response = await fetchWithAuth('/logout', {
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

    userManagementButton.addEventListener('click', () => {
        window.location.href = '/adm/user-management.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });

    newsManagementButton.addEventListener('click', () => {
        window.location.href = '/adm/news-management.html'
    })

    const banModal = document.getElementById('banModal');
    const closeModal = document.getElementsByClassName('close')[0];
    const banEmail = document.getElementById('banEmail');
    const banForm = document.getElementById('banForm');


    deleteAccountButton.addEventListener('click', () => {
        banEmail.value = emailInput.value; // 이메일을 읽기 전용으로 설정
        banModal.style.display = 'block'; // 모달을 보여줌
    });

    // 모달 닫기 이벤트
    closeModal.addEventListener('click', () => {
        banModal.style.display = 'none';
    });

    window.addEventListener('click', (event) => {
        if (event.target == banModal) {
            banModal.style.display = 'none';
        }
    });

    banForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const banReason = document.getElementById('banReason').value;
        const email = emailInput.value;

        const confirmed = confirm('정말 탈퇴시키겠습니까?');
        if (confirmed) {
            try {
                const response = await fetchWithAuth('/v1/admin/users', {
                    method: 'DELETE',
                    headers: {
                        'Authorization': accessToken,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        email: email,
                        reason: banReason
                    })
                });

                const result = await response.json();
                if (response.ok) {
                    alert(result.response);
                    window.location.href = '/adm/user-management.html';
                } else {
                    alert(result.message);
                }
            } catch (error) {
                alert(`Error: ${error.message}`);
            }
        }
    });
});