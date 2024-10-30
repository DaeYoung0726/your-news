document.addEventListener('DOMContentLoaded', () => {
    const createPostButton = document.getElementById('createPostButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const logoutButton = document.getElementById('logoutButton');
    const editInfoButton = document.getElementById('editInfoButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const editSubNewsButton = document.getElementById('editSubNewsButton');
    const deleteAccountButton = document.getElementById('deleteAccountButton');
    const subscribeButton = document.getElementById('subscribeButton');
    const saveSubscribeButton = document.getElementById('saveSubscribeButton');
    const closeModalButton = document.getElementById('closeModalButton');

    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const nicknameInput = document.getElementById('nickname');
    const subNewsInput = document.getElementById('subNews');
    const subStatusInput = document.getElementById('subStatus');
    const accessToken = localStorage.getItem('accessToken');

    function updateSubscriptionText(subStatus, dailySubStatus) {
        let subscriptionText = '';

        if (subStatus && dailySubStatus) {
            subscriptionText = '새 글마다 알림, 하루 한 번 요약';
        } else if (subStatus) {
            subscriptionText = '새 글마다 알림';
        } else if (dailySubStatus) {
            subscriptionText = '하루 한 번 요약';
        } else {
            subscriptionText = '소식 전달 거부';
        }

        subStatusInput.value = subscriptionText;
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
            subNewsInput.innerHTML = data.subNews.map(news => news.news.replace(/\\n/g, '<br>'));

            updateSubscriptionText(data.subStatus, data.dailySubStatus);

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
            const response = await fetch('/v1/auth/logout', {
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
        subscribeModal.style.display = 'block';
    });

    closeModalButton.addEventListener('click', () => {
        subscribeModal.style.display = 'none';
    });

    saveSubscribeButton.addEventListener('click', () => {
        const newPostAlert = document.getElementById('newPostAlert').checked;
        const dailySummaryAlert = document.getElementById('dailySummaryAlert').checked;
        const username = usernameInput.value; // 이메일 값 가져오기

        const subStatus = newPostAlert;
        const dailySubStatus = dailySummaryAlert;

        fetch('/v1/users/subscribe', {
            method: 'PATCH',
            headers: {
                'Authorization': accessToken,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                subStatus: subStatus,
                dailySubStatus: dailySubStatus
            })
        })
            .then(response => response.json())
            .then(updateData => {
                alert('소식 전달 상태가 업데이트되었습니다.');
            })
            .catch(error => console.error('Error updating subscription status:', error));
    });
});
