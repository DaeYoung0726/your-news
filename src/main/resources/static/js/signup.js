document.addEventListener('DOMContentLoaded', () => {
    const signupForm = document.getElementById('signupForm');
    const responseMessage = document.getElementById('responseMessage');
    const news1Select = document.getElementById('news1');
    const news2Select = document.getElementById('news2');
    const sendVerificationCodeButton = document.getElementById('sendVerificationCode');
    const verifyCodeButton = document.getElementById('verifyCode');
    const checkUsernameButton = document.getElementById('checkUsername');
    const checkNicknameButton = document.getElementById('checkNickname');
    const emailError = document.getElementById('emailError');
    const verificationCodeError = document.getElementById('verificationCodeError');
    const usernameError = document.getElementById('usernameError');
    const nicknameError = document.getElementById('nicknameError');
    let isEmailVerified = false;
    let isUsernameChecked = false;
    let isNicknameChecked = false;

    // Populate news options
    fetch('/v1/news')
        .then(response => response.json())
        .then(data => {
            data.forEach(news => {
                let option1 = document.createElement('option');
                option1.text = news.newsName;
                news1Select.add(option1);

                let option2 = document.createElement('option');
                option2.text = news.newsName;
                news2Select.add(option2);
            });
        })
        .catch(error => console.error('Error fetching news options:', error));

    sendVerificationCodeButton.addEventListener('click', async () => {
        const email = document.getElementById('email').value;

        try {
            const response = await fetch(`/v1/email/verification-request?email=${encodeURIComponent(email)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email })
            });

            const result = await response.text(); // 응답을 텍스트로 처리

            if (response.ok) {
                emailError.textContent = result; // 텍스트 응답을 표시
            } else {
                emailError.textContent = `Error: ${result}`;
            }
        } catch (error) {
            emailError.textContent = `Error: ${error.message}`;
        }
    });

    // Verify the code
    verifyCodeButton.addEventListener('click', async () => {
        const email = document.getElementById('email').value;
        const code = document.getElementById('verificationCode').value;

        try {
            const response = await fetch(
                `/v1/email/code-verification?email=${encodeURIComponent(email)}&code=${encodeURIComponent(code)}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, code })
                });

            const result = await response.json();

            if (response.ok) {
                isEmailVerified = true;
                verificationCodeError.textContent = '인증이 완료되었습니다';
                verifyCodeButton.disabled = true; // Disable the verify button
            } else {
                verificationCodeError.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            verificationCodeError.textContent = `Error: ${error.message}`;
        }
    });

    // Check username duplication
    checkUsernameButton.addEventListener('click', async () => {
        const username = document.getElementById('username').value;

        try {
            const response = await fetch(`/v1/users/check-username?username=${encodeURIComponent(username)}`, {
                method: 'GET'
            });

            if (response.ok) {
                const result = await response.json();

                if (response.ok && !result) { // If result.available is false, it means the username is not taken
                    isUsernameChecked = true;
                    usernameError.textContent = '사용 가능한 아이디입니다.';
                } else {
                    isUsernameChecked = false;
                    usernameError.textContent = '사용 불가능한 아이디입니다.';
                }
            } else {
                const errorResult = await response.json();
                isUsernameChecked = false;
                usernameError.textContent = `Error: ${errorResult.message}`;
            }
        } catch (error) {
            isUsernameChecked = false;
            usernameError.textContent = `Error: ${error.message}`;
        }
    });

    // Check nickname duplication
    checkNicknameButton.addEventListener('click', async () => {
        const nickname = document.getElementById('nickname').value;

        try {
            const response = await fetch(`/v1/users/check-nickname?nickname=${encodeURIComponent(nickname)}`, {
                method: 'GET'
            });

            if (response.ok) {
                const result = await response.json();

                if (response.ok && !result) { // If result.available is false, it means the nickname is not taken
                    isNicknameChecked = true;
                    nicknameError.textContent = '사용 가능한 닉네임입니다.';
                } else {
                    isNicknameChecked = false;
                    nicknameError.textContent = '사용 불가능한 닉네임입니다.';
                }
            } else {
                const errorResult = await response.json();
                isNicknameChecked = false;
                nicknameError.textContent = `Error: ${errorResult.message}`;
            }
        } catch (error) {
            isNicknameChecked = false;
            nicknameError.textContent = `Error: ${error.message}`;
        }
    });

    // 비밀번호 유효성 검사
    const isPasswordValid = (password) => {
        const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
        return regex.test(password);
    };

    // Handle form submission
    signupForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        if (!isEmailVerified) {
            responseMessage.textContent = '이메일 인증을 완료해주세요.';
            return;
        }

        if (!isUsernameChecked) {
            responseMessage.textContent = '아이디 중복 확인을 해주세요.';
            return;
        }

        if (!isNicknameChecked) {
            responseMessage.textContent = '닉네임 중복 확인을 해주세요.';
            return;
        }

        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (!isPasswordValid(password)) {
            passwordError.textContent = '비밀번호는 최소 8자 이상, 하나 이상의 대문자, 소문자, 숫자 및 특수 문자를 포함해야 합니다.';
            return;
        } else if (password !== confirmPassword) {
            passwordError.textContent = '비밀번호가 일치하지 않습니다.';
            return;
        } else {
            passwordError.textContent = '';
        }

        // 최소 하나의 뉴스 선택 확인
        const news1 = document.getElementById('news1').value;
        const news2 = document.getElementById('news2').value;
        if (!news1 && !news2) {
            responseMessage.textContent = '적어도 하나의 소식을 선택해주세요.';
            return;
        }

        const subNewsNames = [];
        if (news1) subNewsNames.push(news1);
        if (news2) subNewsNames.push(news2);

        const formData = {
            email: document.getElementById('email').value,
            nickname: document.getElementById('nickname').value,
            username: document.getElementById('username').value,
            password: password,
            subNewsNames: subNewsNames
        };

        try {
            const response = await fetch('/v1/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                alert('회원가입이 성공적으로 완료되었습니다.');
                window.location.href = '/';
            } else {
                const result = await response.json();
                responseMessage.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });
});
