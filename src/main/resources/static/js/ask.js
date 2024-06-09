document.addEventListener("DOMContentLoaded", () => {
    const mainPageButton = document.getElementById('mainPageButton');
    const askForm = document.getElementById('askForm');
    const asker = document.getElementById('asker');

    const accessToken = localStorage.getItem('accessToken');

    fetchWithAuth('/v1/users', {
        method: 'GET',
        headers: {
            'Authorization': accessToken,
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            asker.value = data.email;
        })
        .catch(error => console.error('Error fetching user info:', error));

    mainPageButton.addEventListener('click', () => {
        window.location.href = '/main.html';
    });

    askForm.addEventListener('submit', function(event) {
        event.preventDefault();

        const content = document.getElementById('content').value;

        const askDto = {
            asker: asker.value,
            content: content
        };

        fetch('/v1/email/ask', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(askDto)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                alert("문의하기 성공.");
                window.location.href = '/main.html';
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
                alert('문의하기 실패: ' + error.message);
            });
    });
});
