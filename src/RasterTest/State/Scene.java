package RasterTest.State;

import RasterTest.Animation;
import RasterTest.State.Animation.ToTranslate;
import RasterTest.State.Math.Coord2D;
import RasterTest.State.Math.HomogeneousCoord;
import RasterTest.State.Math.Matrix4x4;

/**
 *  Правила ее преобразования в поле зрения камеры
 */
public class Scene implements Transformation {

    /**
     * Поле, хранящее правила преобразования модели в глобальную систему координат
     */
    private ModelInstance modelInstance;
    /**
     * Поле, хранящее класс, показывающий шаг анимации
     */
    private AnimateModelInstance animationStep;

    /**
     * Камера
     */
    private final Camera camera;

    /**
     * Экран
     */
    private final PointView pointView;

    /**
     * Свет
     */
    private final Light light;

    /**
     * Конструктор от заданный правил.
     * Делегируется дефолтный конструктор (все остальные поля)
     * @param modelInstance класс правил
     */
    public Scene(ModelInstance modelInstance) {
        this();
        this.modelInstance = modelInstance;
    }

    /**
     * Дефолтный конструктор. Все поля (что можно), создаются дефолтно. Остальные вызывают единственный экземпляр
     */
    public Scene() {
        this.modelInstance = new ModelInstance();
        this.animationStep = new AnimateModelInstance();
        this.camera = Camera.fabric();
        this.light = Light.fabric();
        this.pointView = PointView.fabric();
    }

    /**
     * Матрица преобразования Проекции
     * @return Матрица
     */
    private Matrix4x4 M_proj() {
        return pointView.transformation();
    }

    /**
     * Матрица преобразования Камеры
     * @return Матрица
     */
    private Matrix4x4 M_cam() {
        return camera.transformation();
    }

    /**
     * Матрица преобразования объекта в глобальную систему координат. Если стоит флаг анимации, то применяется шаг анимации
     * @return Матрица
     */
    private Matrix4x4 M_mod() {
        if (Animation.isIsAnimate()) {
            modelInstance.animationStep(this.animationStep);
        }
        return modelInstance.transformation();
    }

    /**
     * Итоговая матрица преобразования M = M_proj * M_cam * M_trans в заданном порядке
     * @return Матрица
     */
    @Override
    public Matrix4x4 transformation() {
        return M_proj().multiplyOnMatrix(M_cam()).multiplyOnMatrix(M_mod());
    }


    /**
     * Получение 2D координат из однородной системы координат.
     * Добавлена фича (В совокупности с методом Render.render()):
     * !Когда камера слишком близко к объекту => камера уезжает на нужное расстояние, и весь рендер пересобирается
     * @param coord Однородная система координат
     * @return 2D координату
     */
    public static Coord2D getting2DCoordinate(HomogeneousCoord coord) {
        double z = coord.getZ();
        if (z <= PointView.fabric().getD()) {
            ToTranslate.translate(Camera.fabric().getOffset(), 0, 0, -(PointView.fabric().getD() - z + 1));
            throw new RuntimeException("Camera Translation");
        }
        Coord2D coord2D = new Coord2D(coord.getX() / z, coord.getY() / z, coord.getZ());
        return coord2D;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }

    public ModelInstance getAnimationStep() {
        return animationStep;
    }

    public void setAnimationStep(AnimateModelInstance animationStep) {
        this.animationStep = animationStep;
    }

}
