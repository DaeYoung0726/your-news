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
    const viewTermsButton = document.getElementById('viewTerms');
    const termsCheckbox = document.getElementById('termsCheckbox');
    const termsError = document.getElementById('termsError');
    let isEmailVerified = false;
    let isUsernameChecked = false;
    let isNicknameChecked = false;

    viewTermsButton.addEventListener('click', () => {
        window.open('privacy_agreement.html', '_blank');
    });

    // 소식 불러오기
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

    // 인증코드 보내기
    sendVerificationCodeButton.addEventListener('click', async () => {
        const email = document.getElementById('email').value;

        try {
            const response = await fetch(`/v1/email/verification-request?email=${encodeURIComponent(email)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({email})
            });

            const result = await response.json();

            if (response.ok) {
                emailError.textContent = result.response;
            } else {
                emailError.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            emailError.textContent = `Error: ${error.message}`;
        }
    });

    // 인증코드 확인하기
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
                    body: JSON.stringify({email, code})
                });

            const result = await response.json();

            if (response.ok) {
                isEmailVerified = true;
                verificationCodeError.textContent = '인증이 완료되었습니다';
                verifyCodeButton.disabled = true;
            } else {
                verificationCodeError.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            verificationCodeError.textContent = `Error: ${error.message}`;
        }
    });

    // 아이디 중복 확인
    checkUsernameButton.addEventListener('click', async () => {
        const username = document.getElementById('username').value;

        try {
            const response = await fetch(`/v1/users/check-username?username=${encodeURIComponent(username)}`, {
                method: 'GET'
            });

            const result = await response.json();

            if (response.ok) {

                if (response.ok && !result.response) {
                    isUsernameChecked = true;
                    usernameError.textContent = '사용 가능한 아이디입니다.';
                } else {
                    isUsernameChecked = false;
                    usernameError.textContent = '사용 불가능한 아이디입니다.';
                }
            } else {
                isUsernameChecked = false;
                usernameError.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            isUsernameChecked = false;
            usernameError.textContent = `Error: ${error.message}`;
        }
    });

    // 닉네임 중복 확인
    checkNicknameButton.addEventListener('click', async () => {
        const nickname = document.getElementById('nickname').value;

        try {
            const response = await fetch(`/v1/users/check-nickname?nickname=${encodeURIComponent(nickname)}`, {
                method: 'GET'
            });

            const result = await response.json();
            if (response.ok) {

                if (response.ok && !result.response) { // If result.available is false, it means the nickname is not taken
                    isNicknameChecked = true;
                    nicknameError.textContent = '사용 가능한 닉네임입니다.';
                } else {
                    isNicknameChecked = false;
                    nicknameError.textContent = '사용 불가능한 닉네임입니다.';
                }
            } else {
                isNicknameChecked = false;
                nicknameError.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            isNicknameChecked = false;
            nicknameError.textContent = `Error: ${error.message}`;
        }
    });


    // 회원가입 제출
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

        if (!termsCheckbox.checked) {
            termsError.textContent = '개인정보 이용 약관에 동의해주세요.';
            return;
        }

        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
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

            const result = await response.json();
            if (response.ok) {
                alert('회원가입이 성공적으로 완료되었습니다.');
                window.location.href = '/';
            } else {
                let errorMessages = '';
                for (const [field, message] of Object.entries(result)) {
                    errorMessages += `${field}: ${message}\n`;
                }
                alert(`Error: ${errorMessages}`);
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });
});
