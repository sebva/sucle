<?php

class User
{
    private $id;
    private $inscription;
    private $social_id;
    private $type;
	private $name;
	
    public function __construct($data)
    {
        $this->id = isset($data['id']) ? $data['id'] : null;
        $this->inscription = isset($data['inscription']) ? $data['inscription'] : null;
        $this->social_id = isset($data['social_id']) ? $data['social_id'] : null;
        $this->type = isset($data['type']) ? $data['type'] : null;
		$this->name = isset($data['name']) ? $data['name'] : null;
    }

    public function getInfo()
    {
        return array('id' => $this->id,
            'inscription' => $this->inscription,
            'social_id' => $this->social_id,
            'type' => $this->type,
			'name' => $this->name);
    }

    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * @return mixed
     */
    public function getInscription()
    {
        return $this->inscription;
    }

    /**
     * @param null $id
     */
    public function setId($id)
    {
        $this->id = $id;
    }

    /**
     * @param null $inscription
     */
    public function setInscription($inscription)
    {
        $this->inscription = $inscription;
    }

    /**
     * @param null $social_id
     */
    public function setSocialId($social_id)
    {
        $this->social_id = $social_id;
    }

    /**
     * @return null
     */
    public function getSocialId()
    {
        return $this->social_id;
    }

    /**
     * @param null $type
     */
    public function setType($type)
    {
        $this->type = $type;
    }

    /**
     * @return null
     */
    public function getType()
    {
        return $this->type;
    }

	public function getName()
	{
		return $this->name;
	}
	
	public function setName($name)
	{
		$this->name = $name;
	}
} 