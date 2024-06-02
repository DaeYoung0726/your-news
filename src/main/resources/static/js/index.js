document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('modal');
    const modalBody = document.getElementById('modalBody');
    const closeModal = document.getElementsByClassName('close')[0];

    document.getElementById('findIdLink').addEventListener('click', () => {
        window.location.href = 'findId.html';
    });

    document.getElementById('findPwLink').addEventListener('click', () => {
        window.location.href = 'findPassword.html';
    });

    document.getElementById('signupLink').addEventListener('click', () => {
        modalBody.innerHTML = `
            <h2>회원가입</h2>
            <form id="signupForm">
                <label for="email">이메일:</label>
                <input type="email" id="email" name="email" required>
                <label for="nickname">닉네임:</label>
                <input type="text" id="nickname" name="nickname" required>
                <label for="password">비밀번호:</label>
                <input type="password" id="password" name="password" required>
                <button type="submit">회원가입</button>
            </form>
        `;
        modal.style.display = 'flex';
    });

    closeModal.onclick = function() {
        modal.style.display = 'none';
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }
});
