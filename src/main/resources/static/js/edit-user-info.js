document.addEventListener('DOMContentLoaded', () => {
    const createPostButton = document.getElementById('createPostButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const usernameInput = document.getElementById('username');
    const emailInput = document.getElementById('email');
    const nicknameInput = document.getElementById('nickname');
    const currentPasswordInput = document.getElementById('currentPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const updateInfoButton = document.getElementById('updateInfoButton');
    const responseMessage = document.getElementById('responseMessage');
    const checkNicknameButton = document.getElementById('checkNicknameButton');
    const nicknameError = document.getElementById('nicknameError');
    const accessToken = localStorage.getItem('accessToken');

    let originalNickname = '';

    createPostButton.addEventListener('click', () => {
        window.location.href = '/create-post.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });

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

    // 현재 정보 불러오기
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
            originalNickname = data.nickname;
        })
        .catch(error => console.error('Error fetching member info:', error));

    // 업데이트 폼 제출
    updateInfoButton.addEventListener('click', async () => {
        const nickname = nicknameInput.value;
        const currentPassword = currentPasswordInput.value;
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (newPassword !== confirmPassword) {
            responseMessage.textContent = '새 비밀번호가 일치하지 않습니다.';
            return;
        }

        const memberUpdateDto = {
            nickname: nickname,
            currentPassword: currentPassword,
            newPassword: newPassword
        };

        try {
            const response = await fetchWithAuth('/v1/users', {
                method: 'PUT',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(memberUpdateDto)
            });

            const result = await response.json();
            if (response.ok) {
                alert(result.response);
                window.location.href = '/user-info.html';
            } else {
                let errorMessages = '';
                for (const [field, message] of Object.entries(result)) {
                    errorMessages += `${message}\n`;
                }
                alert(`Error: ${errorMessages}`);
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });

    // 닉네임 중복 확인
    checkNicknameButton.addEventListener('click', async () => {
        const nickname = nicknameInput.value;

        if (!nickname) {
            nicknameError.textContent = '닉네임을 입력해주세요.';
            return;
        }

        if (nickname === originalNickname) {
            nicknameError.textContent = '';
            return;
        }

        try {
            const response = await fetchWithAuth(`/v1/users/check-nickname?nickname=${nickname}`, {
                method: 'GET',
                headers: {
                    'Authorization': accessToken,
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const isAvailable = await response.json();
                if (!isAvailable.response) {
                    nicknameError.textContent = '사용 가능한 닉네임입니다.';
                    nicknameError.style.color = 'green';
                } else {
                    nicknameError.textContent = '이미 사용 중인 닉네임입니다.';
                    nicknameError.style.color = 'red';
                }
            } else {
                nicknameError.textContent = '닉네임 중복 확인 중 오류가 발생했습니다.';
                nicknameError.style.color = 'red';
            }
        } catch (error) {
            nicknameError.textContent = `Error: ${error.message}`;
            nicknameError.style.color = 'red';
        }
    });
});