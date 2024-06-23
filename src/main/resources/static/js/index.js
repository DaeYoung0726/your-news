document.addEventListener('DOMContentLoaded', () => {
    const findIdLink = document.getElementById('findIdLink');
    const findPwLink = document.getElementById('findPwLink');
    const modal = document.getElementById('modal');
    const closeModal = document.getElementsByClassName('close')[0];
    const findIdFormContainer = document.getElementById('findIdFormContainer');
    const findPwFormContainer = document.getElementById('findPwFormContainer');

    modal.style.display = 'none';

    document.getElementById('signupLink').addEventListener('click', () => {
        window.location.href = 'privacy_agreement.html';
    });

    // 모달 열기
    function openModal(formContainer) {
        findIdFormContainer.style.display = 'none';
        findPwFormContainer.style.display = 'none';
        formContainer.style.display = 'block';
        modal.style.display = 'block';
    }

    // 모달 닫기
    closeModal.addEventListener('click', () => {
        modal.style.display = 'none';
    });

    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    // 아이디 찾기
    findIdLink.addEventListener('click', () => {
        openModal(findIdFormContainer);
    });

    const findIdForm = document.getElementById('findIdForm');
    findIdForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const email = document.getElementById('findIdEmail').value;
        const responseMessage = document.getElementById('findIdResponseMessage');

        try {
            const response = await fetch(`/v1/auth/find-username?email=${encodeURIComponent(email)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();
            if (response.ok) {
                responseMessage.textContent = `아이디는 ${result.response} 입니다.`;
            } else {
                responseMessage.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });

    // 비밀번호 찾기
    findPwLink.addEventListener('click', () => {
        openModal(findPwFormContainer);
    });

    const findPasswordForm = document.getElementById('findPasswordForm');
    findPasswordForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const username = document.getElementById('findPwUsername').value;
        const email = document.getElementById('findPwEmail').value;
        const responseMessage = document.getElementById('findPwResponseMessage');

        try {
            const response = await fetch(`/v1/auth/find-pass?email=${encodeURIComponent(email)}&username=${encodeURIComponent(username)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            const result = await response.json();
            if (response.ok) {
                responseMessage.textContent = result.response;
            } else {
                responseMessage.textContent = `Error: ${result.message}`;
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });

    const loginForm = document.getElementById('loginForm');
    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/v1/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, password })
            });

            const result = await response.json();

            if (response.ok) {
                // AccessToken 저장
                localStorage.setItem('accessToken', result.accessToken);
                // main.html로 리디렉션
                window.location.href = 'main.html';
            } else {
                // Handle login error
                modalBody.textContent = `Error: ${result.message}`;
                modal.style.display = 'block';
            }
        } catch (error) {
            modalBody.textContent = `Error: ${error.message}`;
            modal.style.display = 'block';
        }
    });
});
