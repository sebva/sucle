<?php

class Settings
{
    const HOST = 'host';
    const USER = 'user';
    const PASSWORD = 'password';
    const DATABASE = 'database';
    const UPLOAD = 'upload';
    const MAX_SIZE_FILE = 'max_size_file';
    const LEN_MESSAGE_MAX = 'len_message_max';
    const VALID_EXTENSION = 'valid_extension';
    const DEFAULT_NB_MESSAGE = 'default_nb_message';
    const TYPE_SOCIAL = 'type_social';
    const WEBSITE = 'website';

    public static function getSettings()
    {
        return array(
            self::HOST => '127.7.189.130',
            self::USER => 'adminHjGYxtU',
            self::PASSWORD => 'v2tFq4Tdb_15',
            self::DATABASE => 'sucle',
            self::WEBSITE => 'https://sucle-diego999.rhcloud.com',
            self::UPLOAD => 'uploads/',
            self::MAX_SIZE_FILE => 30*1024*1024,
            self::LEN_MESSAGE_MAX => 160,
            self::VALID_EXTENSION => array('video' => array('mp4','mov','m4v','webm','3gp'),
                                            'audio' => array('mp3','3gp','mp4','m4a','ogg','wav'),
                                            'image' => array('jpg','jpeg','gif','png')),
            self::DEFAULT_NB_MESSAGE => 20,
            self::TYPE_SOCIAL => array('FB', 'GP')
        );
    }
}