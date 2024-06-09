document.addEventListener('DOMContentLoaded', async () => {
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const newsManagementButton = document.getElementById('newsManagementButton');
    const userManagementButton = document.getElementById('userManagementButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const submitButton = document.getElementById('submitButton');
    const responseMessage = document.getElementById('responseMessage');
    const accessToken = localStorage.getItem('accessToken');


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

    newsManagementButton.addEventListener('click', () => {
        window.location.href = '/adm/news-management.html'
    })

    userManagementButton.addEventListener('click', () => {
        window.location.href = '/adm/user-management.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });

    submitButton.addEventListener('click', async () => {
        const newsName = document.getElementById('newsName').value;
        const newsURL = document.getElementById('newsURL').value;

        const data = {
            newsName,
            newsURL
        };

        try {
            const response = await fetchWithAuth('/v1/admin/news', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': accessToken
                },
                body: JSON.stringify(data)
            });

            const result = await response.json();
            if (response.ok) {
                responseMessage.style.color = 'green';
                responseMessage.textContent = '소식 추가 성공.';
            } else {
                responseMessage.textContent = `Error:\n${result.message}`;
            }
        } catch (error) {
            responseMessage.style.color = 'red';
            responseMessage.textContent = `Error: ${error.message}`;
        }
    });
});
