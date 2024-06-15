document.addEventListener('DOMContentLoaded', () => {
    const createPostButton = document.getElementById('createPostButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const logoutButton = document.getElementById('logoutButton');
    const editInfoButton = document.getElementById('editInfoButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const editSubNewsButton = document.getElementById('editSubNewsButton');
    const deleteAccountButton = document.getElementById('deleteAccountButton');
    const subscribeButton = document.getElementById('subscribeButton');

    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const nicknameInput = document.getElementById('nickname');
    const subNewsInput = document.getElementById('subNews');
    const subStatusInput = document.getElementById('subStatus');
    const accessToken = localStorage.getItem('accessToken');

    let currentStatus = false; // 현재 소식 수신 상태를 추적할 변수

    // 버튼 스타일 업데이트 함수
    function updateSubscribeButton(status) {
        if (status) {
            subStatusInput.value = "소식 전달 허용 중";
            subscribeButton.textContent = "소식 전달 거부";
            subscribeButton.style.backgroundColor = "red";
            subscribeButton.style.color = "white";
        } else {
            subStatusInput.value = "소식 전달 거부 중";
            subscribeButton.textContent = "소식 전달 허용";
            subscribeButton.style.backgroundColor = "#007bff"; // 진한 파란색
            subscribeButton.style.color = "white";
        }
    }

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

            currentStatus = data.subStatus;

            // subStatus에 따른 버튼 텍스트 및 색상 설정
            updateSubscribeButton(currentStatus);

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

    subscribeButton.addEventListener('click', () => {
        const newStatus = !currentStatus;
        const username = usernameInput.value; // 이메일 값 가져오기

        fetch('/v1/users/subscribe', {
            method: 'PATCH',
            headers: {
                'Authorization': accessToken,
            },
            body: new URLSearchParams({
                username: username,
                value: newStatus
            })
        })
            .then(response => response.json())
            .then(updateData => {
                currentStatus = newStatus; // 상태 업데이트
                // 버튼 텍스트 및 색상 업데이트
                updateSubscribeButton(currentStatus);

                alert('소식 전달 상태가 업데이트되었습니다.');
            })
            .catch(error => console.error('Error updating subscription status:', error));
    });
});
