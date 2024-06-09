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
});
