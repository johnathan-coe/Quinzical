# Managing Categories

## Category Format

Here's an example category file:

```
This is the largest native tree in New Zealand|what is|Kauri
This well known fruit use to be called the Chinese Gooseberry|what is|Kiwifruit
New Zealand has 200 of this species, and one species is our national symbol|what is|fern
The honey from this plant is known for its medicinal benefits|what is|Mānuka
The nectar from bright yellow flowers of this tree are an important food source for native birds|what is|Kowhai
This is known as the New Zealand Christmas tree|what is|Pōhtukawa
This green vegetable is a traditional staple food of the Māori|what is|Pūhā
This is the plant that the Māori weaved into baskets|what is|Harakeke/flax
This introduced alga is a threat to New Zealand rivers and is known as rock snot|what is|Didymo
```

As you can see, each line represents a question and its answer.
Let's break down the first line.

```
This is the largest native tree in New Zealand|what is|Kauri
```

As you can see, the line is split by the `|` character
and is in the following format:

```
Question|Prompt|Answer
```

So if we want to add, for example, the question "This is what the ocean is primarily made of",
where the answer is "[What is] water".
Then we would add the following line:

```
This is what the ocean is primarily made of|what is|water
```

You've successfully written your first question!

A category file _must_ have _at least_ 5 questions
and its name is the same as the filename given to it.
This file should be placed in the `categories` folder.

### Multiple Answers

If you wish to allow multiple possible different answers.
Then write all the answers separated by a `/` character.

e.g.

```
This can be used to remove pencil markings|what is|Eraser/Rubber
```

Quinzical will then mark the user's submissions correct if it matches "Eraser" or "Rubber".

## Modify a Category

Go to the `categories` folder and select a category you wish to edit (its name is the filename).
Open up that file in your favourite text editor and add or remove the lines to add or remove questions respectively.

## Add a Category

Write your category file in your favourite text editor and save it into the `categories` folder
with the same filename as the category's name (_without_ a file extension).

## Remove a Category

Remove the category file in the `categories` folder that corresponds with the category you wish to remove.
Make sure there are at least 5 category files in the `categories` folder which has a name that is _not_ `International`.
The `International` category file serves as a backup file if the game cannot connect to its online quiz bank
and so it serves an important purpose.

