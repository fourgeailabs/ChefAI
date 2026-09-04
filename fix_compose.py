with open("app/src/main/java/com/example/ui/screens/RecipeDetailScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for i in range(len(lines)):
    if i > 0 and lines[i].strip() == "@Composable" and lines[i-1].strip() == "@Composable":
        continue
    new_lines.append(lines[i])

with open("app/src/main/java/com/example/ui/screens/RecipeDetailScreen.kt", "w") as f:
    f.writelines(new_lines)
