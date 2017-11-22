const log = console.log;

// connect to server
const sendToServer = ((endpoint, subscribeAdress, publishAddress, connectCallback, messageCallback) => {
	
	const stompClient = Stomp.over(new SockJS(endpoint));
	
	stompClient.connect({}, (frame) => {
		stompClient.subscribe(subscribeAdress, messageCallback);
		connectCallback();
	});
	
	return (message) => {
		stompClient.send(publishAddress, {}, JSON.stringify(message));
	}
	
})('/pokerNight',
	'/user/queue/private',
	'/app/message',
	() => {
		
		// load game assets
		app.loader
			.add("bkg.jpg", "bkg.jpg")
			.add("dashboard.jpg", "dashboard.jpg")
			.add("deckBig.png", "deckBig.png")
			.add("sprites.json").load(() => {
			
			// init display group
			app.stage.displayList = new PIXI.DisplayList();
			
			// setup stage
			const tableBg = new PIXI.Sprite(PIXI.utils.TextureCache["bkg.jpg"]);
			tableBg.width = app.renderer.width;
			tableBg.height = app.renderer.height;
			tableBg.displayGroup = bgLayer;
			
			const playerBg = new PIXI.Sprite(PIXI.utils.TextureCache["dashboard.jpg"]);
			playerBg.width = app.renderer.width;
			playerBg.height = 100;
			playerBg.y = app.renderer.height - 100;
			playerBg.alpha = 0.9;
			playerBg.displayGroup = bgLayer;
			
			table.addChild(tableBg);
			playerArea.addChild(playerBg);
			
			app.stage.addChild(table);
			app.stage.addChild(playerArea);
			
			//set up animation ticker
			app.ticker.add(delta => {
				table.children.forEach(sprite => {
					if (typeof sprite.tick === 'function') {
						sprite.tick(delta, animationSpeed);
					};
				});
			});
			
			// start the app
			app.start();
			
			console.log(table.width, table.height, table.x, table.y);
			
			// send the registery Message
			sendToServer({type:'REGISTER'});
		});

	},
	(msg) => {
		updateGame(msg);
	}); 

const makeCard = (id, texture, parent, x, y) => { 
	
	const card = makeGameObject(
			id, 
			texture ? PIXI.utils.TextureCache[texture] : null,
			parent,
			x,
			y);
	
	card.anchor.set(0.5);
	card.displayGroup = defaultLayer;

	card.update = (newState, me) => {
		const target = newState.cards.find(x => me.id === x.sprite.id);
		if (target) {
			const {x, y} = target.sprite;
			card.target = new PIXI.Point(x,y);
		}
	};
	
	card.tick = (delta,animationSpeed) => {
		if (card.target && !card.dragging) {
			const {x, y} = card;
			const {x: tx,y: ty} = card.target;
			
			//TODO neatify this,
			
			card.x += (tx - x) * animationSpeed * delta;
			card.y += (ty - y) * animationSpeed * delta;
			
			if (Math.abs(card.x - tx) < 1 && Math.abs(card.y - ty) < 1) {
				card.position = card.target;
				delete card.target;
			}
		}
	}
	
	card.onDragStart = (event) => {

		if (!card.dragging) {
			card.data = event.data;
			card.dragging = true;
			card.displayGroup = dragLayer;
			
			card.alpha = 0.5;
			card.scale.x *= 1.1;
			card.scale.y *= 1.1;
			card.dragPoint = event.data.getLocalPosition(card);
			
			card.xInitial = card.x;
			card.yInitial = card.y;
		}
	};
	
	card.onDragMove = (event) => {
		if (card.dragging) {
			const newPosition = event.data.getLocalPosition(card.parent);
			card.x = newPosition.x - card.dragPoint.x;
			card.y = newPosition.y - card.dragPoint.y;
		}
	};

	card.onDragEnd = () => {
		
		if (card.dragging) {
			card.dragging = false;
			
			const {x, y, id} = card;
			card.target = new PIXI.Point(x,y);
			
	        // notify server
	        sendToServer({'type':'MOVE',id,x,y});
			
			card.displayGroup = defaultLayer;

//			card.x = card.xInitial;
//			card.y = card.yInitial;
			card.alpha = 1;
			card.scale.x /= 1.1;
	        card.scale.y /= 1.1;
	        
	        // set the interaction data to null
	        card.data = null;
		}
	};
	
	makeDraggable(card);
	return card;
}

const makeGameObject = (id, texture = PIXI.Texture.WHITE, parent = null,x = 0, y = 0) => {
	const sprite = new PIXI.Sprite(texture);
	sprite.id = id;
	sprite.x = x;
	sprite.y = y;
	if (parent) {
		parent.addChild(sprite);		
	}
	return sprite;
}

const makeDraggable = (target) => {
	target.interactive = true;
	target.buttonMode = true;
	target
		.on('pointerdown', target.onDragStart)
		.on('pointerup', target.onDragEnd)
		.on('pointerupoutside', target.onDragEnd)
		.on('pointermove', target.onDragMove);	
}


const updateGame= (() => {

	// last state (by using a closure we make sure updateGame is the only method that has access to state
	const currentState = 
		{
			"cards":[]
		};
	
	const toId = (card) => card.sprite.id;
	const findMissingCards = (newCards, oldCards) => {
		return newCards.filter(card => oldCards.map(toId).indexOf(card.sprite.id) === -1);
	};
	const findExistingCards = (newCards, oldCards) => {
		return newCards.filter(card => oldCards.map(toId).indexOf(card.sprite.id) !== -1);
	}
	
	const cards = (latestCards = []) => {

		const missingCards = findMissingCards(latestCards, currentState.cards)
			.map((card) => { 
				const { id, texture, x, y } = card.sprite;
				return makeCard(id, texture, table, x, y);
			});
		const existingCards = findExistingCards(latestCards, currentState.cards);
	};
	
	return (message) => {
				
		// map whole stomp message to body which is the newState
		const newState = JSON.parse(message.body);

		cards(newState.cards);
		
		// finally update new State
		currentState.cards = newState.cards;
		
		// notify components
		table.children.forEach((sprite) => {
			if (typeof sprite.update === 'function') {
				sprite.update(newState, sprite); // have to pass the sprite since can't pass this scope to Array.find(..,thisArg) 
			};
		});
		
		log(currentState);
	};
	
	
})();







