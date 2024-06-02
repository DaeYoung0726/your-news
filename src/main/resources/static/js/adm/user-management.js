document.addEventListener('DOMContentLoaded', async () => {
    const logoutButton = document.getElementById('logoutButton');
    const mainPageButton = document.getElementById('mainPageButton');
    const userManagementButton = document.getElementById('userManagementButton');
    const newsManagementButton = document.getElementById('newsManagementButton');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const userList = document.getElementById('userList');
    const pagination = document.getElementById('pagination');
    const accessToken = localStorage.getItem('accessToken');
    let currentPage = 0;
    const pageSize = 10;

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

    userManagementButton.addEventListener('click', () => {
        window.location.href = '/adm/user-management.html';
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });

    newsManagementButton.addEventListener('click', () => {
        window.location.href = '/adm/news-management.html'
    })

    async function fetchUsers(page) {
        const response = await fetchWithAuth(`/v1/admin/users?page=${page}&size=${pageSize}`, {
            method: 'GET',
            headers: {
                'Authorization': accessToken,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            displayUsers(data.content);
            displayPagination(data);
        } else {
            alert('사용자 목록을 불러오는 데 실패했습니다.');
        }
    }

    function displayUsers(users) {
        userList.innerHTML = '';
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.id}</td>
                <td><a href="/adm/user-info.html?id=${user.id}" class="user-link">${user.nickname}</a></td>
                <td>${user.email}</td>
            `;
            userList.appendChild(row);
        });
    }

    function displayPagination(data) {
        pagination.innerHTML = '';
        for (let i = 0; i < data.totalPages; i++) {
            const pageSpan = document.createElement('span');
            pageSpan.textContent = i + 1;
            pageSpan.classList.add('page');
            if (i === currentPage) {
                pageSpan.classList.add('active');
            }
            pageSpan.addEventListener('click', () => {
                currentPage = i;
                fetchUsers(currentPage);
            });
            pagination.appendChild(pageSpan);
        }
    }

    fetchUsers(currentPage);
});
