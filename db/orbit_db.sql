-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 26, 2026 at 09:26 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `orbit_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `chat_members`
--

CREATE TABLE `chat_members` (
  `conversation_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `role` enum('MEMBER','ADMIN') DEFAULT 'MEMBER',
  `joined_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `left_at` timestamp NULL DEFAULT NULL,
  `encrypted_key` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chat_members`
--

INSERT INTO `chat_members` (`conversation_id`, `user_id`, `nickname`, `role`, `joined_at`, `left_at`, `encrypted_key`) VALUES
(10, 5, NULL, 'MEMBER', '2026-03-26 18:54:53', NULL, NULL),
(10, 6, NULL, 'MEMBER', '2026-03-26 18:54:53', NULL, NULL),
(16, 5, NULL, 'MEMBER', '2026-03-26 19:03:55', NULL, NULL),
(16, 7, NULL, 'MEMBER', '2026-03-26 19:03:55', NULL, NULL),
(17, 6, NULL, 'MEMBER', '2026-03-26 19:08:21', NULL, NULL),
(17, 7, NULL, 'MEMBER', '2026-03-26 19:08:21', NULL, NULL),
(18, 5, NULL, 'MEMBER', '2026-03-26 19:09:19', NULL, 'W5xN/UHxhH9bCVjYZ+jx45Q6n4RI8pUS6k5GazoM+Qo='),
(18, 6, NULL, 'MEMBER', '2026-03-26 19:09:19', NULL, 'zCkTc9BgOuFd+bOzqfURQzk2qwguC+yIO7mFbyF1f8A='),
(18, 7, NULL, 'MEMBER', '2026-03-26 19:09:19', NULL, 'mfPIGtHStL/zORiUisPKkllQSY/KQUuJ/Ja4w+rC8Gk=');

-- --------------------------------------------------------

--
-- Table structure for table `conversations`
--

CREATE TABLE `conversations` (
  `id` int(11) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `group_name` varchar(100) DEFAULT NULL,
  `group_photo_url` varchar(255) DEFAULT NULL,
  `custom_emoji` varchar(10) DEFAULT '?',
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `conversations`
--

INSERT INTO `conversations` (`id`, `type`, `group_name`, `group_photo_url`, `custom_emoji`, `created_by`, `created_at`) VALUES
(10, 'DIRECT', NULL, NULL, '👍', NULL, '2026-03-26 18:54:53'),
(16, 'DIRECT', NULL, NULL, '👍', NULL, '2026-03-26 19:03:55'),
(17, 'DIRECT', NULL, NULL, '👍', NULL, '2026-03-26 19:08:21'),
(18, 'GROUP', 'da', NULL, '👍', NULL, '2026-03-26 19:09:19');

-- --------------------------------------------------------

--
-- Table structure for table `friendships`
--

CREATE TABLE `friendships` (
  `id` int(11) NOT NULL,
  `requester_id` int(11) NOT NULL,
  `receiver_id` int(11) NOT NULL,
  `status` enum('PENDING','ACCEPTED','BLOCKED') DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `friendships`
--

INSERT INTO `friendships` (`id`, `requester_id`, `receiver_id`, `status`, `created_at`, `updated_at`) VALUES
(10, 7, 5, 'ACCEPTED', '2026-03-26 18:49:53', '2026-03-26 18:50:06'),
(11, 6, 5, 'ACCEPTED', '2026-03-26 18:51:18', '2026-03-26 18:51:23'),
(12, 7, 6, 'ACCEPTED', '2026-03-26 18:52:07', '2026-03-26 18:52:12');

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `conversation_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `encrypted_content` longtext DEFAULT NULL,
  `attachment_url` varchar(255) DEFAULT NULL,
  `is_pinned` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`id`, `conversation_id`, `sender_id`, `encrypted_content`, `attachment_url`, `is_pinned`, `created_at`) VALUES
(26, 10, 6, '1nshM0nz4TNKGsRtxYd9YA==', NULL, 0, '2026-03-26 19:08:25'),
(27, 10, 5, 'fFK+c0/nqh17ZdQwbdUaFA==', NULL, 0, '2026-03-26 19:08:48'),
(28, 18, 6, 'ZKxn8Z0kYm82IlODnZ351Q==', NULL, 0, '2026-03-26 19:09:31'),
(29, 18, 5, '5B5Cb1LCevOL8L5khSA1sw==', NULL, 0, '2026-03-26 19:09:37'),
(30, 18, 6, 'RU0jkN7MD21YDFDJFdLIOg==', NULL, 0, '2026-03-26 19:09:43'),
(31, 18, 5, 'xEgXeLkbqf/KIFjwcNLPeQ==', NULL, 0, '2026-03-26 19:10:24'),
(32, 18, 7, 'YjQEiXz+zpf8Jn3pKd8ZYjCJ0SZq6zY7KWhKtsu2GGc=', NULL, 0, '2026-03-26 19:10:54'),
(33, 18, 6, '4xzKEvKFJQHsETEJvGA3Mw==', NULL, 0, '2026-03-26 19:11:07'),
(34, 10, 5, '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', NULL, 0, '2026-03-26 19:31:04'),
(35, 10, 6, 'kCWe/zZWXSC/Tf1GaGrDPA==', NULL, 0, '2026-03-26 19:31:48'),
(36, 10, 6, 'TM0wACLeNtOK5L9n2bw1Lw==', NULL, 0, '2026-03-26 19:31:54'),
(37, 10, 6, '2Ztvp5ZyT2DppdwOcCOFIQ==', NULL, 0, '2026-03-26 19:31:57'),
(38, 10, 6, 'OKKHp14xFPKZPEyJ/gYz+A==', NULL, 0, '2026-03-26 19:32:00'),
(39, 10, 6, 'dlIFUA+UWBGVKVpZ0b+3mA==', NULL, 0, '2026-03-26 19:32:03'),
(40, 10, 6, 'OKKHp14xFPKZPEyJ/gYz+A==', NULL, 0, '2026-03-26 19:32:06'),
(41, 10, 6, 'TD7d4tQmeHK+wOWqXEAZrg==', NULL, 0, '2026-03-26 19:32:11'),
(42, 10, 6, 'kCWe/zZWXSC/Tf1GaGrDPA==', NULL, 0, '2026-03-26 19:35:13'),
(43, 10, 6, 'cHDejC7j0LUi/p+2NpPtaA==', NULL, 0, '2026-03-26 19:35:18'),
(44, 10, 6, 'CXXmSj/7FvYNNkoChNLhUA==', NULL, 0, '2026-03-26 19:35:24'),
(45, 10, 6, '0lt7QN/12jdTZUyokYJfJbOYc91CwcYHK/yrUGzhMdoWHu+GxGGQaO+bmjgL3wCY', NULL, 0, '2026-03-26 19:35:31'),
(46, 18, 5, 'F3zteSZEyqLeC1s99TikgQ==', NULL, 0, '2026-03-26 19:37:32'),
(47, 18, 6, 'xLEPaijTBT4jRNTgP/HrHQ==', NULL, 0, '2026-03-26 19:40:02'),
(48, 18, 6, '+yKyOyvyQ99opXLsPcMJw5DELG089+YJhthAG0yL6KQ=', NULL, 0, '2026-03-26 19:40:03'),
(49, 18, 7, 'xLEPaijTBT4jRNTgP/HrHQ==', NULL, 0, '2026-03-26 19:47:49'),
(50, 18, 7, 'xLEPaijTBT4jRNTgP/HrHQ==', NULL, 0, '2026-03-26 20:19:17'),
(51, 18, 7, '3SdHdxyPFMC0yuR+QuO2NA==', NULL, 0, '2026-03-26 20:19:19'),
(52, 18, 7, 'slarlHe7YQvHAnILEB3CMA==', NULL, 0, '2026-03-26 20:19:20'),
(53, 18, 7, 'bNEWdsP8hAULKRlaxq6+KA==', NULL, 0, '2026-03-26 20:19:21'),
(54, 18, 7, 'zLhf8GCdhL98qmssOpeqsA==', NULL, 0, '2026-03-26 20:19:23'),
(55, 18, 7, 'xLEPaijTBT4jRNTgP/HrHQ==', NULL, 0, '2026-03-26 20:19:26'),
(56, 18, 7, 'xLEPaijTBT4jRNTgP/HrHQ==', NULL, 0, '2026-03-26 20:19:27'),
(57, 18, 7, '3SdHdxyPFMC0yuR+QuO2NA==', NULL, 0, '2026-03-26 20:19:34'),
(58, 18, 7, 'xLEPaijTBT4jRNTgP/HrHQ==', NULL, 0, '2026-03-26 20:21:04');

-- --------------------------------------------------------

--
-- Table structure for table `message_reactions`
--

CREATE TABLE `message_reactions` (
  `message_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `reaction_emoji` varchar(10) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE `posts` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content` text DEFAULT NULL,
  `media_url` varchar(255) DEFAULT NULL,
  `feeling_emoji` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `stories`
--

CREATE TABLE `stories` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `media_url` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `expires_at` timestamp NOT NULL DEFAULT (current_timestamp() + interval 24 hour)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `full_name` varchar(32) NOT NULL,
  `email` varchar(255) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `avatar` longtext DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `full_name`, `email`, `username`, `password`, `created_at`, `avatar`) VALUES
(5, 'Howardo', 'h@gmai.com', 'howard', '1934d8bfaed57f11f8af9175540fce4b2ff0492e6b258e73ae2f16bc8c339d4c', '2026-03-26 08:55:20', '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'),
(6, 'kh', 'k@gmail.com', 'kh', '1fcff1fe7e601e10bb94291b44e0c531fd855f5c9335805f1f7865b576c21828', '2026-03-26 12:17:56', NULL),
(7, 'hello', 'hi@gmail.com', 'hel', 'd6a81f224bbf2f7c22baddbd5d40730eb20cfb0b3d74e10cab61788214caceb1', '2026-03-26 17:59:36', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chat_members`
--
ALTER TABLE `chat_members`
  ADD PRIMARY KEY (`conversation_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `conversations`
--
ALTER TABLE `conversations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `friendships`
--
ALTER TABLE `friendships`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_friendship` (`requester_id`,`receiver_id`),
  ADD KEY `receiver_id` (`receiver_id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `conversation_id` (`conversation_id`),
  ADD KEY `sender_id` (`sender_id`);

--
-- Indexes for table `message_reactions`
--
ALTER TABLE `message_reactions`
  ADD PRIMARY KEY (`message_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `stories`
--
ALTER TABLE `stories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `conversations`
--
ALTER TABLE `conversations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `friendships`
--
ALTER TABLE `friendships`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `stories`
--
ALTER TABLE `stories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `chat_members`
--
ALTER TABLE `chat_members`
  ADD CONSTRAINT `chat_members_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `chat_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `conversations`
--
ALTER TABLE `conversations`
  ADD CONSTRAINT `conversations_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `friendships`
--
ALTER TABLE `friendships`
  ADD CONSTRAINT `friendships_ibfk_1` FOREIGN KEY (`requester_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `friendships_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `message_reactions`
--
ALTER TABLE `message_reactions`
  ADD CONSTRAINT `message_reactions_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `message_reactions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `posts`
--
ALTER TABLE `posts`
  ADD CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `stories`
--
ALTER TABLE `stories`
  ADD CONSTRAINT `stories_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
