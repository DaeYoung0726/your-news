document.addEventListener('DOMContentLoaded', () => {
    const findPasswordForm = document.getElementById('findPasswordForm');
    const responseMessage = document.getElementById('responseMessage');

    findPasswordForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;

        try {
            const response = await fetch(`/v1/auth/find-pass?email=${encodeURIComponent(email)}&username=${encodeURIComponent(username)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (response.ok) {
                const message = await response.text();
                responseMessage.textContent = message;
            } else {
                const error = await response.json();
                responseMessage.textContent = `Error: ${error.message}`;
            }
        } catch (error) {
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });
});
