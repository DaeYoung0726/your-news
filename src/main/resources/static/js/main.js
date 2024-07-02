document.addEventListener('DOMContentLoaded', async () => {
    const logoutButton = document.getElementById('logoutButton');
    const askToAdminButton = document.getElementById('askToAdminButton');
    const createPostButton = document.getElementById('createPostButton');
    const noticeButton = document.getElementById('noticeButton');
    const newsRequestButton = document.getElementById('newsRequestButton');
    const noticePosts = document.getElementById('noticePosts');
    const newsRequestPosts = document.getElementById('newsRequestPosts');
    const noticePostsContainer = document.getElementById('noticePostsContainer');
    const newsRequestPostsContainer = document.getElementById('newsRequestPostsContainer');
    const fetchInfoButton = document.getElementById('fetchInfoButton');
    const adminButtons = document.getElementById('adminButtons');
    const noticePagination = document.getElementById('noticePagination');
    const newsRequestPagination = document.getElementById('newsRequestPagination');
    const accessToken = localStorage.getItem('accessToken');
    let currentPage = 0;
    let totalPages = 0;
    const pagesPerBlock = 10;
    let currentCategory = 'notice'; // 현재 선택된 카테고리
    let userRole = ''; // 사용자 역할

    async function checkAuthorization() {

        try {
            // 사용자 권한 확인
            const roleResponse = await fetchWithAuth('/v1/auth/user-role', {
                method: 'GET',
                headers: {
                    'Authorization': accessToken
                }
            });

            const roleData = await roleResponse.json();
            userRole = roleData.role;

            if (userRole === 'ADMIN') {
                adminButtons.style.display = 'block';

                // 관리자 버튼에 이벤트 리스너 추가
                const newsManagementButton = document.getElementById('newsManagementButton');
                const userManagementButton = document.getElementById('userManagementButton');

                newsManagementButton.addEventListener('click', () => {
                    window.location.href = '../adm/news-management.html';
                });

                userManagementButton.addEventListener('click', () => {
                    window.location.href = '../adm/user-management.html';
                });
            }
        } catch (error) {
            alert('권한이 없습니다. 로그인 페이지로 이동합니다.');
            window.location.href = '/';
        }
    }

    // 페이지 로드 시 권한 확인
    await checkAuthorization();


    document.getElementById('menuButton').addEventListener('click', function() {
        document.getElementById('dropdownContent').classList.toggle('show');
    });

    window.onclick = function(event) {
        if (!event.target.matches('.dropbtn')) {
            var dropdowns = document.getElementsByClassName('dropdown-content');
            for (var i = 0; i < dropdowns.length; i++) {
                var openDropdown = dropdowns[i];
                if (openDropdown.classList.contains('show')) {
                    openDropdown.classList.remove('show');
                }
            }
        }
    }

    document.getElementById('openManualButton').addEventListener('click', function() {
        document.getElementById('manualModal').style.display = 'block';
        fetch('manual.html')
            .then(response => response.text())
            .then(data => {
                document.getElementById('manualContent').innerHTML = data;
            })
            .catch(error => console.error('Error loading manual:', error));
    });

    document.querySelector('.modal .close').addEventListener('click', function() {
        document.getElementById('manualModal').style.display = 'none';
    });

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

    createPostButton.addEventListener('click', () => {
        window.location.href = '/create-post.html';
    });

    noticeButton.addEventListener('click', () => {
        noticeButton.classList.add('active');
        newsRequestButton.classList.remove('active');
        noticePosts.classList.remove('hidden');
        newsRequestPosts.classList.add('hidden');
        currentPage = 0;
        currentCategory = 'notice';
        loadPosts(currentCategory, currentPage);
    });

    newsRequestButton.addEventListener('click', () => {
        newsRequestButton.classList.add('active');
        noticeButton.classList.remove('active');
        newsRequestPosts.classList.remove('hidden');
        noticePosts.classList.add('hidden');
        currentPage = 0;
        currentCategory = 'news-request';
        loadPosts(currentCategory, currentPage);
    });

    fetchInfoButton.addEventListener('click', () => {
        window.location.href = '/user-info.html';
    });

    askToAdminButton.addEventListener('click', () => {
        window.location.href = '/ask.html';
    });

    function loadPosts(category, page) {
        const url = `/v1/${category}/posts?page=${page}&size=10&sort=id,DESC`;

        fetchWithAuth(url)
            .then(response => response.json())
            .then(data => {
                const posts = data.content; // Assuming the response is a Page object
                totalPages = data.totalPages; // Total number of pages
                if (category === 'notice') {
                    noticePostsContainer.innerHTML = '';
                    posts.forEach(post => {
                        const postElement = document.createElement('tr');
                        postElement.innerHTML = `
                            <td>${post.id}</td>
                            <td><a href="/post.html?id=${post.id}" class="post-link">${post.title}</a></td>
                             ${userRole === 'ADMIN' ? `
                            <td>
                                <a href="/adm/user-info.html?nickname=${post.writer}" class="user-link">${post.writer}</a>
                            </td>` : `<td>${post.writer}</td>`}
                            <td>${post.likeCount}</td>
                        `;
                        noticePostsContainer.appendChild(postElement);
                    });
                    displayPagination(noticePagination, totalPages, category);
                } else if (category === 'news-request') {
                    newsRequestPostsContainer.innerHTML = '';
                    posts.forEach(post => {
                        const postElement = document.createElement('tr');
                        postElement.innerHTML = `
                            <td>${post.id}</td>
                            <td><a href="/post.html?id=${post.id}" class="post-link">${post.title}</a></td>
                            ${userRole === 'ADMIN' ? `
                            <td>
                                <a href="/adm/user-info.html?nickname=${post.writer}" class="user-link">${post.writer}</a>
                            </td>` : `<td>${post.writer}</td>`}
                            <td>${post.likeCount}</td>
                        `;
                        newsRequestPostsContainer.appendChild(postElement);
                    });
                    displayPagination(newsRequestPagination, totalPages, category);
                }
            })
            .catch(error => console.error(`Error fetching ${category} posts:`, error));
    }

    function displayPagination(paginationContainer, totalPages, category) {
        paginationContainer.innerHTML = '';
        const totalBlocks = Math.ceil(totalPages / pagesPerBlock);
        const currentBlock = Math.floor(currentPage / pagesPerBlock);

        // 이전 블록 버튼
        if (currentBlock > 0) {
            const prevBlockButton = document.createElement('span');
            prevBlockButton.textContent = '<';
            prevBlockButton.classList.add('page');
            prevBlockButton.classList.add('prev');
            prevBlockButton.addEventListener('click', () => {
                currentPage = (currentBlock - 1) * pagesPerBlock;
                loadPosts(category, currentPage);
            });
            paginationContainer.appendChild(prevBlockButton);
        }

        // 현재 블록의 페이지 버튼
        const startPage = currentBlock * pagesPerBlock;
        const endPage = Math.min(startPage + pagesPerBlock, totalPages);
        for (let i = startPage; i < endPage; i++) {
            const pageSpan = document.createElement('span');
            pageSpan.textContent = i + 1;
            pageSpan.classList.add('page');
            if (i === currentPage) {
                pageSpan.classList.add('active');
            }
            pageSpan.addEventListener('click', () => {
                currentPage = i;
                loadPosts(category, currentPage);
            });
            paginationContainer.appendChild(pageSpan);
        }

        // 다음 블록 버튼
        if (currentBlock < totalBlocks - 1) {
            const nextBlockButton = document.createElement('span');
            nextBlockButton.textContent = '>';
            nextBlockButton.classList.add('page');
            nextBlockButton.classList.add('next');
            nextBlockButton.addEventListener('click', () => {
                currentPage = (currentBlock + 1) * pagesPerBlock;
                loadPosts(category, currentPage);
            });
            paginationContainer.appendChild(nextBlockButton);
        }
    }

    loadPosts(currentCategory, currentPage);
});
