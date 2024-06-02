document.addEventListener('DOMContentLoaded', () => {
    const findIdForm = document.getElementById('findIdForm');
    const responseMessage = document.getElementById('responseMessage');

    findIdForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const email = document.getElementById('email').value;

        try {
            const response = await fetch(`/v1/auth/find-username?email=${encodeURIComponent(email)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const username = await response.text();
                responseMessage.textContent = `아이디는 ${username} 입니다.`;
            } else {
                const error = await response.json();
                responseMessage.textContent = `Error: ${error.message}`;
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });
});
