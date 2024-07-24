-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: localhost:3307
-- Thời gian đã tạo: Th1 15, 2024 lúc 02:45 PM
-- Phiên bản máy phục vụ: 8.0.30
-- Phiên bản PHP: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `book`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `author`
--

CREATE TABLE `author` (
  `id` bigint NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `birth_day` date DEFAULT NULL,
  `fullname` varchar(255) DEFAULT NULL,
  `nationality` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `author`
--

INSERT INTO `author` (`id`, `address`, `birth_day`, `fullname`, `nationality`) VALUES
(1, 'Hà nội', '1998-10-02', 'Nguyễn quang sáng', 'Việt nam'),
(2, 'new York', '1975-10-02', 'Lê hồng quang', 'Mỹ');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `book`
--

CREATE TABLE `book` (
  `id` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `number_page` int DEFAULT NULL,
  `publish_year` int DEFAULT NULL,
  `author_id` bigint DEFAULT NULL,
  `genres_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `book`
--

INSERT INTO `book` (`id`, `description`, `name`, `number_page`, `publish_year`, `author_id`, `genres_id`) VALUES
(1, 'Đây là mô tả sách t.a', 'Tiếng anh chuyên ngành CNTT', 150, 2015, 1, 1),
(2, 'Đây là mô tả sách java spring', 'Lập trình java spring boot cơ bản', 170, 2017, 2, 2),
(3, 'Đây là mô tả sách java core', 'Lập trình java core update', 155, 2016, 1, 2);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `book_mark`
--

CREATE TABLE `book_mark` (
  `id` bigint NOT NULL,
  `book_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `book_mark`
--

INSERT INTO `book_mark` (`id`, `book_id`, `user_id`) VALUES
(1, 1, 6);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `comment`
--

CREATE TABLE `comment` (
  `id` bigint NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `star` float DEFAULT NULL,
  `book_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `comment`
--

INSERT INTO `comment` (`id`, `content`, `created_date`, `star`, `book_id`, `user_id`) VALUES
(1, 'Bình luận sách số 1', '2024-01-15 21:33:50', 4.5, 1, 5),
(3, 'Bình luận sách số 1', '2024-01-15 21:36:21', 4.5, 1, 5);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `genres`
--

CREATE TABLE `genres` (
  `id` bigint NOT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `genres`
--

INSERT INTO `genres` (`id`, `name`) VALUES
(1, 'Tiếng anh'),
(2, 'Lập trình'),
(3, 'Tin học');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `user`
--

CREATE TABLE `user` (
  `id` bigint NOT NULL,
  `fullname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `actived` bit(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Đang đổ dữ liệu cho bảng `user`
--

INSERT INTO `user` (`id`, `fullname`, `password`, `role`, `username`, `actived`) VALUES
(1, 'admin', '12345', 'ROLE_ADMIN', 'admin@gmail.com', b'1'),
(5, 'Trần Văn Hiếu', '12345', 'ROLE_USER', 'hieu@gmail.com', b'1'),
(6, 'Trần Văn Nam', '12345', 'ROLE_USER', 'nam@gmail.com', b'1');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `author`
--
ALTER TABLE `author`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `book`
--
ALTER TABLE `book`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKklnrv3weler2ftkweewlky958` (`author_id`),
  ADD KEY `FKslo2ix081ao1w3dk5t8ng1gk9` (`genres_id`);

--
-- Chỉ mục cho bảng `book_mark`
--
ALTER TABLE `book_mark`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKb1js1mt596ypy5s3gchy5v6fd` (`book_id`),
  ADD KEY `FKtg5yr81hxe59aiaotkf76qd4v` (`user_id`);

--
-- Chỉ mục cho bảng `comment`
--
ALTER TABLE `comment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKkko96rdq8d82wm91vh2jsfak7` (`book_id`),
  ADD KEY `FK8kcum44fvpupyw6f5baccx25c` (`user_id`);

--
-- Chỉ mục cho bảng `genres`
--
ALTER TABLE `genres`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `author`
--
ALTER TABLE `author`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `book`
--
ALTER TABLE `book`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `book_mark`
--
ALTER TABLE `book_mark`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT cho bảng `comment`
--
ALTER TABLE `comment`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `genres`
--
ALTER TABLE `genres`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `book`
--
ALTER TABLE `book`
  ADD CONSTRAINT `FKklnrv3weler2ftkweewlky958` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`),
  ADD CONSTRAINT `FKslo2ix081ao1w3dk5t8ng1gk9` FOREIGN KEY (`genres_id`) REFERENCES `genres` (`id`);

--
-- Các ràng buộc cho bảng `book_mark`
--
ALTER TABLE `book_mark`
  ADD CONSTRAINT `FKb1js1mt596ypy5s3gchy5v6fd` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  ADD CONSTRAINT `FKtg5yr81hxe59aiaotkf76qd4v` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Các ràng buộc cho bảng `comment`
--
ALTER TABLE `comment`
  ADD CONSTRAINT `FK8kcum44fvpupyw6f5baccx25c` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  ADD CONSTRAINT `FKkko96rdq8d82wm91vh2jsfak7` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
