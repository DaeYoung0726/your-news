document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modalBody');
    const closeModal = document.getElementsByClassName('close')[0];

    document.getElementById('findIdLink').addEventListener('click', () => {
        window.location.href = 'find-username.html';
    });

    document.getElementById('findPwLink').addEventListener('click', () => {
        window.location.href = 'find-password.html';
    });

    document.getElementById('signupLink').addEventListener('click', () => {
        window.location.href = 'signup.html';
    });

    closeModal.onclick = function() {
        modal.style.display = 'none';
    };

    window.onclick = function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };

    // Login form submission
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
